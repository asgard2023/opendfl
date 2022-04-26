package org.ccs.opendfl.core.limitlock;


import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import org.ccs.opendfl.core.biz.IRequestLockConfigBiz;
import org.ccs.opendfl.core.config.RequestLockConfiguration;
import org.ccs.opendfl.core.constants.ReqLockType;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FrequencyException;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.utils.EtcdUtil;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
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
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
    @Resource(name = "requestLockConfigBiz")
    private IRequestLockConfigBiz requestLockConfigBiz;
    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplateString;
    // etcl客户端链接
    @Autowired
    private Client etcdClient = null;
    public static final Map<String, RequestLockVo> locksMap = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final AtomicInteger counter = new AtomicInteger();
    private final ThreadLocal<String> lockRandomId = new ThreadLocal<>();
    private final ThreadLocal<String> lockDataId = new ThreadLocal<>();
    private final ThreadLocal<String> etcdLockKey = new ThreadLocal<>();
    private final ThreadLocal<Long> etcdReleaseKey = new ThreadLocal<>();

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

        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequestLock reqLimit = handlerMethod.getMethodAnnotation(RequestLock.class);
            if (reqLimit == null) {
                return true;
            }

            long curTime = System.currentTimeMillis();
            String requestUri = RequestUtils.getRequestUri(request);
            RequestLockVo requestLockVo = newRequestLockVo(reqLimit, requestUri, curTime);
            requestLockConfigBiz.loadLockConfig(requestLockVo, curTime);
            logFirstload(requestLockVo);

            String attrName = RequestParams.USER_ID;
            if (StringUtils.isNotBlank(requestLockVo.getAttrName())) {
                attrName = requestLockVo.getAttrName();
            }
            final Integer time = requestLockVo.getTime();
            final String errMsg = requestLockVo.getErrMsg();
            Map<String, Object> reqParams = RequestUtils.getParamsObject(request);
            String dataId = FrequencyUtils.getAttrNameValue(reqParams, attrName);

            if (dataId != null) {
                lockDataId.set(dataId);
                String ip = RequestUtils.getIpAddress(request);
                Integer count = counter.incrementAndGet();
                String rndId = count + "-" + random.nextInt(1000);
                String redisKey=null;
                boolean isLimit=false;
                if(ReqLockType.ETCD==reqLimit.lockType()) {
                    redisKey = FrequencyUtils.getEtcdKeyLock(reqLimit.name(), dataId);
                    long leaseId = grantLease(time);
                    etcdReleaseKey.set(leaseId);
                    String lockKeyStr=EtcdUtil.lock(redisKey, leaseId);
                    isLimit=true;
                    etcdLockKey.set(lockKeyStr);
                }
                else{
                    redisKey = FrequencyUtils.getRedisKeyLock(reqLimit.name(), dataId);
                    isLimit = redisTemplateString.opsForValue().setIfAbsent(redisKey, rndId, time, TimeUnit.SECONDS);
                }
                if (isLimit) {
                    logger.debug("----preHandle--name={} time={} dataId={}, rndId={}", reqLimit.name(), time, dataId, rndId);
                    lockRandomId.set(rndId);
                } else {
                    logger.warn("----preHandle--redisKey={} time={} dataId={} ip={} limited", redisKey, time, dataId, ip);
                    String title = "frequency:lock";
                    BaseException baseException = new FrequencyException("重复任务限制:" + String.format(errMsg, dataId));
                    baseException.setTitle(title);
                    throw baseException;
                }
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("-----preHandle--handler={}", handler.getClass().getName());
            throw e;
        }
        return true;
    }
    private long grantLease(long ttl) throws Exception {
        CompletableFuture<LeaseGrantResponse> feature = etcdClient.getLeaseClient().grant(ttl);
        LeaseGrantResponse response = feature.get();
        return response.getID();
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
            if(ReqLockType.REDIS==reqLimit.lockType()){
                String redisKey = FrequencyUtils.getRedisKeyLock(reqLimit.name(), dataId);
                String v = redisTemplateString.opsForValue().get(redisKey);
                logger.debug("----afterCompletion--dataId={} rndId={} v={}", dataId, rndId, v);
                if (StringUtils.equals(rndId, v)) {
                    redisTemplateString.delete(redisKey);
                }
            }
            else if(ReqLockType.ETCD==reqLimit.lockType()) {
                String lockKey = etcdLockKey.get();
                if (lockKey != null) {
                    etcdClient.getLeaseClient().revoke(etcdReleaseKey.get());
                    etcdClient.getLockClient().unlock(ByteSequence.from(lockKey, StandardCharsets.UTF_8));
                }
            }
        }
    }

    private boolean isNoLimit(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //支持关闭频率限制，可用于测试环境，ST环境
        if (!StringUtils.ifYes(requestLockConfiguration.getIfActive())) {
            return true;
        }
        return false;
    }

}
