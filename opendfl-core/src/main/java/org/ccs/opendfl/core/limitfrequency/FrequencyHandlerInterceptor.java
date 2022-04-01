package org.ccs.opendfl.core.limitfrequency;


import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.strategy.black.BlackChain;
import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FrequencyHandlerInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(FrequencyHandlerInterceptor.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    @Autowired
    private FreqLimitChain freqLimitChain;
    @Autowired
    private WhiteChain whiteChain;
    @Autowired
    private BlackChain blackChain;
    private static final String BLACK_LIST_INFO = "{\"resultCode\":\"100010\",\"errorMsg\":\"Frequency limit\",\"data\":\"WaT+azid/F/83e1UpLc6ZA==\",\"errorType\":\"biz\",\"success\":false}";

    private ThreadLocal<Long> startTime = new ThreadLocal<>();
    private ThreadLocal<String> requestKey = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof DefaultServletHttpRequestHandler || handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        //支持关闭频率限制，可用于测试环境，ST环境
        if (!StringUtils.ifYes(frequencyConfiguration.getIfActive())) {
            return true;
        }

        String requestUri = RequestUtils.getRequestUri(request);
        FrequencyVo frequencyVo = null;
        try {

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Map<String, Object> params = new HashMap<>();
            String remoteIp = RequestUtils.getIpAddress(request);
            String ip = getConvertIp(remoteIp);
            String lang = RequestUtils.getLang(request);
            Long curTime = System.currentTimeMillis();
            startTime.set(curTime);

            selectStrategyItems();

            loadReqParamsOnce(request, params);

            RequestStrategyParamsVo strategyParams = new RequestStrategyParamsVo(request, lang, ip, handlerMethod.getMethod().getName(), requestUri, curTime);
            String userId = (String) params.get(RequestParams.USER_ID);
            strategyParams.setUserId(userId);

            RequestVo requestVo = this.logFirstLoadRequest(handlerMethod, request.getMethod(), strategyParams);

            blackChain.setStrategyParams(strategyParams);
            blackChain.setBlackConfig(frequencyConfiguration.getBlack());
            blackChain.clearLimit();
            boolean isBlack = blackChain.doCheckLimit(blackChain);
            if (isBlack) {
                logger.warn("----preHandle--uri={} blackIp={} ", request.getRequestURI(), remoteIp);
                response.getWriter().println(BLACK_LIST_INFO);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }


            whiteChain.setStrategyParams(strategyParams);
            whiteChain.setWhiteConfig(frequencyConfiguration.getWhite());
            whiteChain.clearLimit();
            boolean isWhite = whiteChain.doCheckLimit(whiteChain);
            if (isWhite) {
                if (FrequencyUtils.isInitLog("preHandle")) {
                    logger.info("----preHandle--white-uri={}", requestUri);
                }
                return true;
            }

            freqLimitChain.setStrategyParams(strategyParams);
            frequencyVo = new FrequencyVo();
            frequencyVo.setRequestUri(requestUri);
            boolean isAllNull = this.limitByFrequency(handlerMethod, frequencyVo, strategyParams, params, response);
            if (isAllNull) {
                this.limitByRequestConfig(requestVo, frequencyVo, strategyParams, params, response);
            }

            strategyParams = null;
            frequencyVo = null;
            params = null;
            return true;
        } catch (BaseException e) {
            logger.error("-----preHandle--uri={} error={}", requestUri, e.getMessage());
            throw e;
        } catch (Exception e) {
            String reqInfo = frequencyVo != null ? frequencyVo.toString() : "{}";
            logger.error("-----preHandle--uri={} reqInfo={} error={}", requestUri, reqInfo, e.getMessage());
            throw e;
        }
    }

    /**
     * 按配置顺序选取有配置的策略
     */
    private void selectStrategyItems() {
        String freqTypeItems = frequencyConfiguration.getLimit().getItems();
        freqTypeItems = CommUtils.appendComma(freqTypeItems);
        freqLimitChain.sortStrategies(freqTypeItems);

        String whiteItems = frequencyConfiguration.getWhite().getItems();
        whiteItems = CommUtils.appendComma(whiteItems);
        whiteChain.sortStrategies(whiteItems);

        String blackItems = frequencyConfiguration.getBlack().getItems();
        blackItems = CommUtils.appendComma(blackItems);
        blackChain.sortStrategies(blackItems);
    }

    /**
     * 基于@Frequency注释的限制
     *
     * @param handlerMethod
     * @param frequencyVo
     * @param strategyParams
     * @param params
     * @param response
     * @return
     */
    private boolean limitByFrequency(HandlerMethod handlerMethod, FrequencyVo frequencyVo, RequestStrategyParamsVo strategyParams, Map<String, Object> params, HttpServletResponse response) {
        boolean isAllNull = true;
        FrequencyVo retVo = null;
        Frequency methodFrequency = handlerMethod.getMethodAnnotation(Frequency.class);
        retVo = FrequencyVo.toFrequencyVo(methodFrequency, frequencyVo);
        isAllNull = retVo == null && isAllNull;
        handleFrequency(response, params, retVo, strategyParams);

        Frequency2 methodFrequency2 = handlerMethod.getMethodAnnotation(Frequency2.class);
        retVo = FrequencyVo.toFrequencyVo(methodFrequency2, frequencyVo);
        isAllNull = retVo == null && isAllNull;
        handleFrequency(response, params, retVo, strategyParams);

        Frequency3 methodFrequency3 = handlerMethod.getMethodAnnotation(Frequency3.class);
        retVo = FrequencyVo.toFrequencyVo(methodFrequency3, frequencyVo);
        isAllNull = retVo == null && isAllNull;
        handleFrequency(response, params, retVo, strategyParams);
        return isAllNull;
    }

    /**
     * 基于uri地址的频率限制
     *
     * @param frequencyVo
     * @param strategyParams
     * @param params
     * @param response
     */
    private void limitByRequestConfig(RequestVo requestVo, FrequencyVo frequencyVo, RequestStrategyParamsVo strategyParams, Map<String, Object> params, HttpServletResponse response) {
        FrequencyConfigUtils.limitBySysconfig(requestVo);
        List<LimitUriConfigVo> limitConfigList = requestVo.getLimitRequests();
        for (LimitUriConfigVo uriConfigVo : limitConfigList) {
            frequencyVo = FrequencyVo.toFrequencyVo(frequencyVo, uriConfigVo);
            handleFrequency(response, params, frequencyVo, strategyParams);
        }
    }

    private String getConvertIp(String ip) {
        try {
            if (!(RequestUtils.isIpv6Address(ip) || "localhost".equals(ip))) {
                ip = "" + RequestUtils.getIpConvertNum(ip);
            }
        } catch (Exception e) {
            logger.warn("----handleFrequency--ip={} error={}", ip, e.getMessage());
        }
        return ip;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long requestStartTime = this.startTime.get();
        if (requestStartTime == null) {
            return;
        }
        if(frequencyConfiguration.getMinRunTime()==0){
            startTime.remove();
            requestKey.remove();
            return;
        }
        String key = requestKey.get();
        startTime.remove();
        requestKey.remove();
        Long runTime = System.currentTimeMillis() - requestStartTime;
        //记录超出最小执行时间的最大值
        if (runTime > frequencyConfiguration.getMinRunTime()) {
            RequestVo requestVo = requestVoMap.get(key);
            //排除等一次请求
            if (requestVo != null && requestVo.getCounter().get() > 1) {
                if(requestVo.getMaxRunTime()==null || requestVo.getMaxRunTime() < runTime){
                    requestVo.setMaxRunTime(runTime);
                    requestVo.setMaxRunTimeCreateTime(requestStartTime);
                }
            }
        }

    }


    private boolean handleFrequency(HttpServletResponse response, Map<String, Object> params, FrequencyVo frequency, RequestStrategyParamsVo strategyParams) {
        boolean going = true;
        if (frequency == null) {
            return going;
        }
        Long curTime = strategyParams.getCurTime();


        FrequencyConfigUtils.limitBySysconfigLoad(frequency, curTime);
        final int limit = frequency.getLimit();
        final int time = frequency.getTime();
        if (time == 0 || limit == 0) {
            logger.warn("----handleFrequency--uri={} time={} limit={} invalid", strategyParams.getRequestUri(), time, limit);
            try {
                response.getWriter().println(BLACK_LIST_INFO);
            } catch (IOException e) {
                logger.warn("----handleFrequency--uri={} error={}", strategyParams.getRequestUri(), e.getMessage());
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        String userId = strategyParams.getUserId();
        String attrName = RequestParams.USER_ID;
        if (StringUtils.isNotBlank(frequency.getAttrName())) {
            attrName = frequency.getAttrName();
        }
        Object attrValue = params.get(attrName);
        if (attrValue != null) {
            userId = "" + attrValue;
            params.put(RequestParams.USER_ID, userId);
        }

        logFirstload(frequency, curTime);

        strategyParams.load(frequency, userId);

        /**
         * 这里的顺序没有关系，主要由SYS_TEST_FREQUENT_ITEM这个参数来控制是否启用及顺序
         */
        freqLimitChain.clearLimit();
        freqLimitChain.doCheckLimit(freqLimitChain);
        return going;
    }

    /**
     * 只从请求中获取参数一次
     *
     * @param request
     * @param params
     */
    private void loadReqParamsOnce(HttpServletRequest request, Map<String, Object> params) {
        Map<String, Object> reqParams = RequestUtils.getParamsObject(request);
        params.putAll(reqParams);
    }


    public static final Map<String, FrequencyVo> freqMap = new ConcurrentHashMap<>(50);
    public static final Map<String, RequestVo> requestVoMap = new ConcurrentHashMap<>(100);

    /**
     * 首次加载日志一下
     *
     * @param frequency
     */
    private void logFirstload(FrequencyVo frequency, Long curTime) {
        String key = frequency.getName() + ":" + frequency.getTime();
        if (!freqMap.containsKey(key)) {
            frequency.setCreateTime(curTime);
            freqMap.put(key, frequency.toCopy());
            logger.info("----logFirstload--redisPrefix={} name={} time={} limit={} ipUser={} userIp={}", frequencyConfiguration.getRedisPrefix(), frequency.getName(), frequency.getTime(), frequency.getLimit(), frequency.getIpUserCount(), frequency.getUserIpCount());
        }
    }

    private RequestVo logFirstLoadRequest(HandlerMethod handlerMethod, String method, RequestStrategyParamsVo strategyParams) {
        String key = strategyParams.getRequestUri() + "." + method;
        requestKey.set(key);
        RequestVo requestVo = requestVoMap.get(key);
        if (requestVo == null) {
            requestVo = new RequestVo();
            requestVo.setCounter(new AtomicInteger());
            requestVo.setRequestUri(strategyParams.getRequestUri());
            requestVo.setBeanName(handlerMethod.getBeanType().getSimpleName());
            requestVo.setMethod(method);
            requestVo.setMethodName(handlerMethod.getMethod().getName());
            requestVo.setCreateTime(strategyParams.getCurTime());
            requestVoMap.put(key, requestVo);
        }
        requestVo.getCounter().incrementAndGet();
        return requestVo;
    }

}
