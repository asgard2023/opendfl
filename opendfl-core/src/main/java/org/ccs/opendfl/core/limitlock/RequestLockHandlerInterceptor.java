package org.ccs.opendfl.core.limitlock;


import org.ccs.opendfl.core.biz.IOutLogBiz;
import org.ccs.opendfl.core.biz.IRequestLockConfigBiz;
import org.ccs.opendfl.core.config.OpendflConfiguration;
import org.ccs.opendfl.core.config.RequestLockConfiguration;
import org.ccs.opendfl.core.constants.DataSourceType;
import org.ccs.opendfl.core.constants.ReqLockType;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FrequencyException;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.locktools.EtcdUtil;
import org.ccs.opendfl.core.utils.locktools.LockUtils;
import org.ccs.opendfl.core.utils.locktools.RedisLockUtils;
import org.ccs.opendfl.core.utils.locktools.ZkLocker;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式锁处理
 * 当任务限制，如果同dataId的任务未处理完，后面的不能处理
 *
 * @author chenjh
 */
@Service
public class RequestLockHandlerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLockHandlerInterceptor.class);
    @Autowired
    private RequestLockConfiguration requestLockConfiguration;
    @Autowired
    private OpendflConfiguration opendflConfiguration;
    @Resource(name = "requestLockConfigBiz")
    private IRequestLockConfigBiz requestLockConfigBiz;
    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplateString;
    @Resource(name = "outLogBiz")
    private IOutLogBiz outLogBiz;
    public void setRequestLockConfigBiz(IRequestLockConfigBiz requestLockConfigBiz){
        this.requestLockConfigBiz = requestLockConfigBiz;
    }

    public void setOutLogBiz(IOutLogBiz outLogBiz) {
        this.outLogBiz = outLogBiz;
    }

    public static final Map<String, RequestLockVo> locksMap = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final AtomicInteger counter = new AtomicInteger();
    private final ThreadLocal<String> lockRandomId = new ThreadLocal<>();
    private final ThreadLocal<String> lockDataId = new ThreadLocal<>();
    private final ThreadLocal<String> etcdLockKey = new ThreadLocal<>();
    private final ThreadLocal<Long> etcdReleaseKey = new ThreadLocal<>();
    private final ThreadLocal<ZkLocker> zkLocker = new ThreadLocal<>();

    private RequestLockVo newRequestLockVo(RequestLock requestLimit, String requestUri, long curTime) {
        RequestLockVo lockVo = RequestLockVo.toLockVo(requestLimit);
        lockVo.setRequestUri(requestUri);
        lockVo.setCreateTime(curTime);
        return lockVo;
    }

    /**
     * 首次加载日志一下
     *
     * @param requestLock
     */
    private void logFirstload(RequestLockVo requestLock) {
        String key = requestLock.getName();
        if (!locksMap.containsKey(key)) {
            locksMap.put(key, requestLock.toCopy());
            logger.info("----logFirstload--name={} time={}", requestLock.getName(), requestLock.getTime());
        }
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (isNoLimit(handler)) {
            return true;
        }

        RequestLockVo requestLockVo = null;
        String attrName = opendflConfiguration.getDefaultAttrName();
        String attrValue = null;
        String ip = null;
        String userId = null;
        String deviceId = null;
        String sysType=null;
        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequestLock reqLimit = handlerMethod.getMethodAnnotation(RequestLock.class);
            if (reqLimit == null) {
                return true;
            }

            long curTime = System.currentTimeMillis();
            String requestUri = RequestUtils.getRequestUri(request);
            sysType = RequestUtils.getSysType(request);
            requestLockVo = newRequestLockVo(reqLimit, requestUri, curTime);
            requestLockConfigBiz.loadLockConfig(requestLockVo, curTime);
            logFirstload(requestLockVo);

            if (StringUtils.isNotBlank(requestLockVo.getAttrName())) {
                attrName = requestLockVo.getAttrName();
            }
            final Integer time = requestLockVo.getTime();
            final String errMsg = requestLockVo.getErrMsg();
            Map<String, Object> reqParams = RequestUtils.getParamsObject(request);
            attrValue = FrequencyUtils.getAttrNameValue(reqParams, attrName);

            if (attrValue != null) {
                lockDataId.set(attrValue);
                ip = RequestUtils.getIpAddress(request);
                Integer count = counter.incrementAndGet();
                String rndId = count + "-" + random.nextInt(1000);
                boolean isLimit = false;
                userId = (String) reqParams.get(RequestParams.USER_ID);
                deviceId = RequestUtils.getDeviceId(request);
                String lockKey = LockUtils.getLockKey(reqLimit, attrValue);
                if (StringUtils.equals(DataSourceType.ETCD.getType(), reqLimit.lockType().getSource())) {
                    isLimit = lockEtcd(reqLimit, lockKey, time, rndId);
                } else if (ReqLockType.ZK == reqLimit.lockType()) {
                    ZkLocker lock = new ZkLocker(reqLimit, lockKey);
                    isLimit = lock.lock();
                    zkLocker.set(lock);
                } else {
                    isLimit = RedisLockUtils.lock(reqLimit, lockKey, rndId);
                }
                if (isLimit) {
                    logger.debug("----preHandle--name={} time={} dataId={}, rndId={}", reqLimit.name(), time, attrValue, rndId);
                    lockRandomId.set(rndId);
                } else {
                    logger.warn("----preHandle--lockKey={} time={} dataId={} ip={} limited", lockKey, time, attrValue, ip);
                    String title = "frequency:lock";
                    BaseException baseException = new FrequencyException("重复任务限制:" + String.format(errMsg, attrValue));
                    baseException.setTitle(title);

                    ip = RequestUtils.convertIpv4(ip);
                    outLogBiz.addLockLog(requestLockVo, userId, ip, deviceId, sysType, attrName, attrValue, null);
                    throw baseException;
                }
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("-----preHandle--error={}", e.getMessage());
            ip = RequestUtils.convertIpv4(ip);
            outLogBiz.addLockLog(requestLockVo, userId, ip, deviceId, sysType, attrName, attrValue, e.toString());
            if (attrValue != null) {
                lockDataId.remove();
            }
            throw e;
        }
        return true;
    }

    /**
     * 请前前etcd加索
     *
     * @param redisKey
     * @param time
     * @param rndId
     * @return
     * @throws Exception
     */
    private boolean lockEtcd(RequestLock reqLimit, String redisKey, Integer time, String rndId) throws Exception {
        boolean isLimit = false;
        long leaseId = EtcdUtil.grantLease(time);
        etcdReleaseKey.set(leaseId);
        if (ReqLockType.ETCD_LOCK == reqLimit.lockType()) {
            String lockKeyStr = EtcdUtil.lock(redisKey, leaseId);
            isLimit = true;
            etcdLockKey.set(lockKeyStr);
        } else if (ReqLockType.ETCD_KV == reqLimit.lockType()) {
            isLimit = EtcdUtil.putKVIfAbsent(redisKey, rndId, leaseId);
        } else {
            logger.warn("-----lockEtcd--invalid name={} lockType={}", reqLimit.name(), reqLimit.lockType());
        }
        return isLimit;
    }


    /**
     * 请求完成后etcd释放锁
     *
     * @param reqLimit
     * @param dataId
     * @param rndId
     * @throws Exception
     */
    private void unlockEtcd(RequestLock reqLimit, String dataId, String rndId) throws Exception {
        logger.debug("----unlockEtcd--lockType={} lockKey={}", reqLimit.lockType(), etcdLockKey.get());
        if (ReqLockType.ETCD_LOCK == reqLimit.lockType()) {
            etcdReleaseKey.remove();
            String lockKey = etcdLockKey.get();
            if (lockKey != null) {
                etcdLockKey.remove();
                EtcdUtil.unlock(lockKey);
            }
        } else if (ReqLockType.ETCD_KV == reqLimit.lockType()) {
            String lockKey = LockUtils.getLockKey(reqLimit, dataId);
            String v = EtcdUtil.getKV(lockKey);
            if (StringUtils.equals(rndId, v)) {
                EtcdUtil.deleteKV(lockKey);
            }
        } else {
            logger.warn("-----unlockEtcd--invalid name={} lockType={}", reqLimit.name(), reqLimit.lockType());
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (isNoLimit(handler)) {
            return;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestLock reqLimit = handlerMethod.getMethodAnnotation(RequestLock.class);
        if (reqLimit == null) {
            return;
        }
        String dataId = lockDataId.get();
        if (dataId != null) {
            lockDataId.remove();
            String rndId = lockRandomId.get();
            if (rndId != null) {
                lockRandomId.remove();
            }
            logger.debug("----afterCompletion--dataId={} rndId={}", dataId, rndId);
            if (ReqLockType.REDIS == reqLimit.lockType()) {
                String lockKey = LockUtils.getLockKey(reqLimit, dataId);
                RedisLockUtils.unlock(lockKey, rndId);
            } else if (ReqLockType.ZK == reqLimit.lockType()) {
                ZkLocker lock = zkLocker.get();
                lock.unlock();
                zkLocker.remove();
                logger.debug("----unlock--zk--redisKey={}", lock.getLockKey());
            } else if (StringUtils.equals(DataSourceType.ETCD.getType(), reqLimit.lockType().getSource())) {
                unlockEtcd(reqLimit, dataId, rndId);
            }
        }
    }


    private boolean isNoLimit(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //支持关闭频率限制，可用于测试环境，ST环境
        return !StringUtils.ifYes(requestLockConfiguration.getIfActive());
    }

}
