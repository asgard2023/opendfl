package org.ccs.opendfl.core.limitlock;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.ccs.opendfl.core.config.RequestLockConfiguration;
import org.ccs.opendfl.core.config.vo.RequestLockConfigVo;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FrequencyException;
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
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 当任务限制，如果同dataId的任务未处理完，后面的不能处理
 */
@Service
public class RequestLockHandlerInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(RequestLockHandlerInterceptor.class);
    @Autowired
    private RequestLockConfiguration requestLockConfiguration;
    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplateString;
    public static final Map<String, RequestLockVo> locksMap = new ConcurrentHashMap<>();
    private Random random = new Random();
    private AtomicInteger counter = new AtomicInteger();
    private ThreadLocal<String> lockRandomId = new ThreadLocal<>();
    private ThreadLocal<String> lockDataId = new ThreadLocal<>();

    private RequestLockVo newRequestLockVo(RequestLock requestLimit, String requestUri, Long curTime) {
        RequestLockVo lockVo = new RequestLockVo();
        lockVo.setRequestUri(requestUri);
        lockVo.setCreateTime(curTime);
        lockVo.setName(requestLimit.name());
        lockVo.setTime(requestLimit.time());
        lockVo.setAttrName(requestLimit.attrName());
        lockVo.setErrMsg(requestLimit.errMsg());
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
            locksMap.put(key, requestLock.clone());
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

            Long curTime = System.currentTimeMillis();
            String requestUri = RequestUtils.getRequestUri(request);
            RequestLockVo requestLockVo = newRequestLockVo(reqLimit, requestUri, curTime);
            loadLockConfig(requestLockVo, curTime);
            logFirstload(requestLockVo);

            final String attrName = requestLockVo.getAttrName();
            final Integer time = requestLockVo.getTime();
            final String errMsg = requestLockVo.getErrMsg();
            String dataId = request.getParameter(attrName);
            if (dataId == null) {
                String body = RequestUtils.getRequestParams(request);
                if (StringUtils.isNotBlank(body)) {
                    JSONObject jsonObject = JSON.parseObject(body);
                    dataId = jsonObject.getString(attrName);
                }
            }


            if (dataId != null) {
                lockDataId.set(dataId);
                String ip = RequestUtils.getIpAddress(request);
                Integer count = counter.incrementAndGet();
                String rndId = count + "-" + random.nextInt(1000);
                String redisKey = getRedisKey(reqLimit, dataId);
                boolean isLimit = redisTemplateString.opsForValue().setIfAbsent(redisKey, rndId);
                if (isLimit) {
                    logger.debug("----preHandle--name={} time={} dataId={}, rndId={}", reqLimit.name(), time, dataId, rndId);
                    lockRandomId.set(rndId);
                    redisTemplateString.expire(redisKey, time, TimeUnit.SECONDS);
                } else {
                    logger.warn("----preHandle--redisKey={} time={} dataId={} ip={} limited", redisKey, time, dataId, ip);
                    throw new FrequencyException("重复任务限制:" + String.format(errMsg, dataId));
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

    private static Map<String, Long> loadSysconfigTimeMap = new ConcurrentHashMap<>();
    private static Map<String, RequestLockConfigVo> sysconfigLimitMap = new ConcurrentHashMap<>();

    private void loadLockConfig(RequestLockVo requestLockVo, Long curTime) {
        String key = requestLockVo.getName();
        RequestLockConfigVo lockConfigVo = sysconfigLimitMap.get(key);
        Long time = loadSysconfigTimeMap.get(key);
        if (time == null || curTime - time > 10000) {
            loadSysconfigTimeMap.put(key, curTime);
            List<RequestLockConfigVo> lockConfigVos = requestLockConfiguration.getLockConfigs();
            if(lockConfigVos==null){
                return;
            }
            for (RequestLockConfigVo lockConfig : lockConfigVos) {
                if (StringUtils.equals(requestLockVo.getName(), lockConfig.getName())) {
                    lockConfigVo = lockConfig;
                    sysconfigLimitMap.put(key, lockConfigVo);
                }
            }
        }
        if (lockConfigVo != null) {
            if (StringUtils.isNotBlank(lockConfigVo.getAttrName())) {
                requestLockVo.setAttrName(lockConfigVo.getAttrName());
            }
            if (lockConfigVo.getTime() != null) {
                requestLockVo.setTime(lockConfigVo.getTime());
            }
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
            String redisKey = getRedisKey(reqLimit, dataId);
            String v = redisTemplateString.opsForValue().get(redisKey);
            if (StringUtils.equals(rndId, v)) {
                redisTemplateString.delete(redisKey);
            }
        }
    }

    private boolean isNoLimit(Object handler) {
        if (!StringUtils.ifYes(requestLockConfiguration.getIfActive())) {
            return true;
        }
        if (handler instanceof DefaultServletHttpRequestHandler || handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        return false;
    }

    private String getRedisKey(RequestLock reqLimit, String dataId) {
        return requestLockConfiguration.getRedisPrefix() + ":" + reqLimit.name() + ":" + dataId;
    }


}
