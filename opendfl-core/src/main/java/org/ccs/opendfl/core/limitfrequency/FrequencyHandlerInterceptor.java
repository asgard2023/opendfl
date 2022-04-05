package org.ccs.opendfl.core.limitfrequency;


import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IFrequencyConfigBiz;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 频率限制处理
 * 白名单，黑名单，IP限制，用户访问限制等
 *
 * @author chenjh
 */
@Service
@Slf4j
public class FrequencyHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;
    @Resource(name = "frequencyConfigBiz")
    private IFrequencyConfigBiz frequencyConfigBiz;

    @Autowired
    private FreqLimitChain freqLimitChain;
    @Autowired
    private WhiteChain whiteChain;
    @Autowired
    private BlackChain blackChain;
    private static final String BLACK_LIST_INFO = "{\"resultCode\":\"100010\",\"errorMsg\":\"Frequency limit\",\"data\":\"WaT+azid/F/83e1UpLc6ZA==\",\"errorType\":\"%s\",\"success\":false}";

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final ThreadLocal<String> requestKey = new ThreadLocal<>();

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
        RequestVo requestVo = null;
        try {

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String remoteIp = RequestUtils.getIpAddress(request);
            String ip = getConvertIp(remoteIp);
            String lang = RequestUtils.getLang(request);
            Long curTime = System.currentTimeMillis();
            startTime.set(curTime);

            selectStrategyItems();

            Map<String, Object> params = RequestUtils.getParamsObject(request);

            RequestStrategyParamsVo strategyParams = new RequestStrategyParamsVo(lang, ip, handlerMethod.getMethod().getName(), requestUri, curTime);
            String userId = (String) params.get(RequestParams.USER_ID);
            strategyParams.setUserId(userId);

            requestVo = this.logFirstLoadRequest(handlerMethod, request.getMethod(), strategyParams);

            blackChain.setStrategyParams(strategyParams);
            blackChain.clearLimit();
            boolean isBlack = blackChain.doCheckLimit(blackChain);
            if (isBlack) {
                log.warn("----preHandle--uri={} blackIp={} ", request.getRequestURI(), remoteIp);
                String title = "frequency:black";
                if (blackChain.getBlackStrategy() != null) {
                    title = "frequency:" + blackChain.getBlackStrategy().getLimitType();
                }
                this.frequencyReturn(requestVo, true);
                response.getWriter().println(String.format(BLACK_LIST_INFO, title));
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }


            whiteChain.setStrategyParams(strategyParams);
            whiteChain.clearLimit();
            boolean isWhite = whiteChain.doCheckLimit(whiteChain);
            if (isWhite) {
                if (FrequencyUtils.isInitLog("preHandle")) {
                    log.info("----preHandle--white-uri={}", requestUri);
                }
                this.frequencyReturn(requestVo, false);
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
            log.error("-----preHandle--uri={} error={}", requestUri, e.getMessage());
            frequencyReturn(requestVo, true);
            throw e;
        } catch (Exception e) {
            frequencyReturn(requestVo, true);
            String reqInfo = frequencyVo != null ? frequencyVo.toString() : "{}";
            log.error("-----preHandle--uri={} reqInfo={} error={}", requestUri, reqInfo, e.getMessage());
            throw e;
        }
    }

    /**
     * 结束处理
     * @param requestVo RequestVo
     */
    private void frequencyReturn(RequestVo requestVo, boolean isFail) {
        startTime.remove();
        requestKey.remove();
        if (isFail && requestVo != null) {
            requestVo.getLimitCounter().incrementAndGet();
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
     * @param handlerMethod HandlerMethod
     * @param frequencyVo FrequencyVo
     * @param strategyParams RequestStrategyParamsVo
     * @param params Map<String, Object>
     * @param response HttpServletResponse
     * @return boolean
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
     * @param requestVo RequestVo
     * @param frequencyVo FrequencyVo
     * @param strategyParams RequestStrategyParamsVo
     * @param params Map<String, Object>
     * @param response HttpServletResponse
     */
    private void limitByRequestConfig(RequestVo requestVo, FrequencyVo frequencyVo, RequestStrategyParamsVo strategyParams, Map<String, Object> params, HttpServletResponse response) {
        frequencyConfigBiz.limitBySysconfigUri(requestVo);
        List<LimitUriConfigVo> limitConfigList = requestVo.getLimitRequests();
        for (LimitUriConfigVo uriConfigVo : limitConfigList) {
            frequencyVo = FrequencyVo.toFrequencyVo(frequencyVo, uriConfigVo);
            frequencyVo.setSysconfig(true);
            handleFrequency(response, params, frequencyVo, strategyParams);
        }
    }

    private String getConvertIp(String ip) {
        try {
            if (!(RequestUtils.isIpv6Address(ip))) {
                ip = "" + RequestUtils.getIpConvertNum(ip);
            }
        } catch (Exception e) {
            log.warn("----handleFrequency--ip={} error={}", ip, e.getMessage());
        }
        return ip;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long requestStartTime = this.startTime.get();
        if (requestStartTime == null) {
            return;
        }
        if (frequencyConfiguration.getMinRunTime() == 0) {
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
                //上次时间如果超过30秒，则清空，以便于重算
                if (requestVo.getMaxRunTimeCreateTime() > requestStartTime - frequencyConfiguration.getMaxRunTimeInterval()*1000) {
                    requestVo.setMaxRunTime(0L);
                    requestVo.setMaxRunTimeCreateTime(0L);
                }
                if (requestVo.getMaxRunTime() < runTime) {
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


        frequencyConfigBiz.limitBySysconfigLoad(frequency, curTime);
        final int limit = frequency.getLimit();
        final int time = frequency.getTime();
        if (time == 0 || limit == 0) {
            log.warn("----handleFrequency--uri={} time={} limit={} invalid", strategyParams.getRequestUri(), time, limit);
            try {
                response.getWriter().println(BLACK_LIST_INFO);
            } catch (IOException e) {
                log.warn("----handleFrequency--uri={} error={}", strategyParams.getRequestUri(), e.getMessage());
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        String userId = strategyParams.getUserId();
        String attrName = RequestParams.USER_ID;
        if (StringUtils.isNotBlank(frequency.getAttrName())) {
            attrName = frequency.getAttrName();
        }
        String attrValue = FrequencyUtils.getAttrNameValue(params, attrName);

        if (attrValue != null) {
            userId = attrValue;
            params.put(RequestParams.USER_ID, userId);
        }

        logFirstload(frequency, curTime);

        strategyParams.load(frequency, userId);

        //这里的顺序没有关系，主要由frequency.limit.items这个参数来控制是否启用及顺序
        freqLimitChain.clearLimit();
        freqLimitChain.doCheckLimit(freqLimitChain);
        return going;
    }

    /**
     * 主要按接口缓存，理论上接口数不会太多，用Map做持久缓存，不会占太多内存
     */
    public static final Map<String, FrequencyVo> freqMap = new ConcurrentHashMap<>(64);
    public static final Map<String, RequestVo> requestVoMap = new ConcurrentHashMap<>(100);

    /**
     * 首次加载日志一下
     *
     * @param frequency FrequencyVo
     */
    private void logFirstload(FrequencyVo frequency, Long curTime) {
        String key = frequency.getName() + ":" + frequency.getTime();
        if (!freqMap.containsKey(key)) {
            frequency.setCreateTime(curTime);
            freqMap.put(key, frequency.toCopy());
            log.info("----logFirstload--redisPrefix={} name={} time={} limit={} ipUser={} userIp={}", frequencyConfiguration.getRedisPrefix(), frequency.getName(), frequency.getTime(), frequency.getLimit(), frequency.getIpUserCount(), frequency.getUserIpCount());
        }
    }

    private RequestVo logFirstLoadRequest(HandlerMethod handlerMethod, String method, RequestStrategyParamsVo strategyParams) {
        String key = strategyParams.getRequestUri() + "." + method;
        requestKey.set(key);
        RequestVo requestVo = requestVoMap.get(key);
        if (requestVo == null) {
            requestVo = new RequestVo();
            requestVo.setCounter(new AtomicInteger());
            requestVo.setLimitCounter(new AtomicInteger());
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
