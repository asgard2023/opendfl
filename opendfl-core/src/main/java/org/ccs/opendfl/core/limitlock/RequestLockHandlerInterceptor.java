package org.ccs.opendfl.core.limitlock;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.ccs.opendfl.core.config.RequestLockConfiguration;
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
    protected static final Map<String, String> limitDataMap = new ConcurrentHashMap<>();
    public static final Map<String, RequestLockVo> locksMap = new ConcurrentHashMap<>();
    private Random random = new Random();
    private AtomicInteger counter = new AtomicInteger();

    /**
     * 首次加载日志一下
     *
     * @param requestLimit
     */
    private void logFirstload(RequestLock requestLimit, String requestUri) {
        String key = requestLimit.name() + ":" + requestLimit.time();
        if (!locksMap.containsKey(key)) {
            RequestLockVo lockVo = new RequestLockVo();
            lockVo.setRequestUri(requestUri);
            lockVo.setCreateTime(System.currentTimeMillis());
            lockVo.setName(requestLimit.name());
            lockVo.setTime(requestLimit.time());
            lockVo.setAttrName(requestLimit.attrName());
            lockVo.setErrMsg(requestLimit.errMsg());
            locksMap.put(key, lockVo);
            logger.info("----logFirstload--name={} time={}", requestLimit.name(), requestLimit.time());
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

            String requestUri = RequestUtils.getRequestUri(request);
            logFirstload(reqLimit, requestUri);
            String dataId = request.getParameter(reqLimit.attrName());
            if (dataId == null) {
                String body = RequestUtils.getRequestParams(request);
                if (StringUtils.isNotBlank(body)) {
                    JSONObject jsonObject = JSON.parseObject(body);
                    dataId = jsonObject.getString(reqLimit.attrName());
                    if (dataId != null) {
                        request.setAttribute(reqLimit.attrName(), dataId);
                    }
                }
            }


            if (dataId != null) {
                String ip = RequestUtils.getIpAddress(request);
                Integer count = counter.incrementAndGet();
                String rndId = count + "-" + random.nextInt(1000);
                String redisKey = getRedisKey(reqLimit, dataId);
                boolean isLimit = redisTemplateString.opsForValue().setIfAbsent(redisKey, rndId);

                if (isLimit) {
                    logger.debug("----preHandle--name={} time={} dataId={}, rndId={}", reqLimit.name(), reqLimit.time(), dataId, rndId);
                    limitDataMap.put(ip + dataId, rndId);
                    redisTemplateString.expire(redisKey, reqLimit.time(), TimeUnit.SECONDS);
                } else {
                    logger.warn("----preHandle--redisKey={} time={} dataId={} ip={} limited", redisKey, reqLimit.time(), dataId, ip);
                    throw new FrequencyException("重复任务限制:" + String.format(reqLimit.errMsg(), dataId));
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
        String dataId = request.getParameter(reqLimit.attrName());
        if (dataId == null) {
            dataId = (String) request.getAttribute(reqLimit.attrName());
        }
        if (dataId != null) {
            String ip = RequestUtils.getIpAddress(request);
            String rndId = limitDataMap.get(ip + dataId);
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
