package org.ccs.opendfl.core.limitfrequency;


import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IFrequencyConfigBiz;
import org.ccs.opendfl.core.biz.IUserBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.OpendflConfiguration;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.FrequencyType;
import org.ccs.opendfl.core.constants.OutLimitType;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FrequencyAttrNameBlankException;
import org.ccs.opendfl.core.strategy.black.BlackChain;
import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.ChainOperVo;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
    private IUserBiz userBiz;

    @Autowired
    private FreqLimitChain freqLimitChain;
    @Autowired
    private WhiteChain whiteChain;
    @Autowired
    private BlackChain blackChain;
    @Autowired
    private RunCountTask runCountTask;

    public void setFrequencyConfigBiz(IFrequencyConfigBiz frequencyConfigBiz) {
        this.frequencyConfigBiz = frequencyConfigBiz;
    }

    public void setFreqLimitChain(FreqLimitChain freqLimitChain) {
        this.freqLimitChain = freqLimitChain;
    }

    public void setWhiteChain(WhiteChain white) {
        this.whiteChain = white;
    }

    private void setBlackChain(BlackChain black) {
        this.blackChain = black;
    }


    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final ThreadLocal<String> requestKey = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (isNoLimit(handler)) {
            return true;
        }

        String requestUri = RequestUtils.getRequestUri(request);
        FrequencyVo frequencyVo = null;
        RequestVo requestVo = null;
        RequestStrategyParamsVo strategyParams = null;
        try {

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String remoteIp = RequestUtils.getIpAddress(request);
            String ip = getConvertIp(remoteIp);
            String lang = RequestUtils.getLang(request);
            long curTime = System.currentTimeMillis();
            startTime.set(curTime);

            selectStrategyItems();

            Map<String, Object> params = RequestUtils.getParamsObject(request);
            String deviceId = RequestUtils.getDeviceId(request);
            String sysType = RequestUtils.getSysType(request);
            String origin = RequestUtils.getOrigin(request);
            strategyParams = new RequestStrategyParamsVo(lang, ip, origin, deviceId, handlerMethod.getMethod().getName(), requestUri, sysType, curTime);
            String userId = (String) params.get(RequestParams.USER_ID);
            strategyParams.setUserId(userId);

            requestVo = this.logFirstLoadRequest(handlerMethod, request.getMethod(), strategyParams);
            //支持关闭频率限制，可用于测试环境，ST环境
            if (!StringUtils.ifYes(frequencyConfiguration.getIfActive())) {
                return true;
            }

            ChainOperVo chainOper = strategyParams.getChainOper();
            //黑名单处理
            chainOper.clearChain();
            boolean isBlack = blackChain.doCheckLimit(blackChain, strategyParams);
            String limitType = null;
            if (isBlack) {
                log.warn("----preHandle--black:{} uri={} ip={} ", limitType, request.getRequestURI(), remoteIp);
                WhiteBlackCheckType freqLimitType = null;
                String title = "frequency:black";
                if (chainOper.getBlackStrategy() != null) {
                    limitType = chainOper.getBlackStrategy().getLimitType();
                    title = "frequency:black:" + limitType;
                    freqLimitType = WhiteBlackCheckType.parseCode(limitType);
                    FrequencyUtils.addFreqLog(strategyParams, 1, 0, OutLimitType.BLACK, freqLimitType);
                }
                this.frequencyReturn(requestVo, true);
                FrequencyUtils.outLimitCount(strategyParams, freqLimitType);
                String errMsg = FrequencyUtils.getFailErrMsg(OutLimitType.BLACK, title, frequencyVo, lang);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().println(errMsg);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }


            //白名单处理
            chainOper.clearChain();
            boolean isWhite = whiteChain.doCheckLimit(whiteChain, strategyParams);
            if (isWhite) {
                limitType = chainOper.getWhiteStrategy().getLimitType();
                if (FrequencyUtils.isInitLog("preHandle")) {
                    log.info("----preHandle--white:{}-uri={} ip={}", limitType, requestUri, ip);
                }
                this.frequencyReturn(requestVo, false);
                WhiteBlackCheckType freqLimitType = WhiteBlackCheckType.parseCode(limitType);
                FrequencyUtils.outLimitCount(strategyParams, freqLimitType);
                FrequencyUtils.addFreqLog(strategyParams, 1, 0, OutLimitType.WHITE, freqLimitType);
                return true;
            }
            if (chainOper.isFail()) {
                this.frequencyReturn(requestVo, true);
                limitType = chainOper.getWhiteStrategy().getLimitType();
                WhiteBlackCheckType freqLimitType = WhiteBlackCheckType.parseCode(limitType);
                FrequencyUtils.outLimitCount(strategyParams, freqLimitType);
                FrequencyUtils.addFreqLog(strategyParams, 1, 0, OutLimitType.WHITE, freqLimitType);
                String title = "frequency:white:" + limitType;
                String errMsg = FrequencyUtils.getFailErrMsg(OutLimitType.WHITE, title, frequencyVo, lang);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().println(errMsg);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            //是否异步
            if(frequencyConfiguration.isAsync()) {
                //基于注解的频率限制
                this.limitByFrequencyAsync(handlerMethod, strategyParams, params, response);

                //基于yml配置的频率限制处理
                this.limitByUriConfigAsync(requestVo, strategyParams, params, response);
            }
            else{
                //基于注解的频率限制
                this.limitByFrequency(handlerMethod, strategyParams, params, response);

                //基于yml配置的频率限制处理
                this.limitByUriConfig(requestVo, strategyParams, params, response);
            }

            strategyParams = null;
            frequencyVo = null;
            params = null;
            return true;
        } catch (FrequencyAttrNameBlankException e) {
            log.warn("-----preHandle--uri={} error={}", requestUri, e.getMessage());
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
     *
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
     * @param handlerMethod  HandlerMethod
     * @param strategyParams RequestStrategyParamsVo
     * @param params         Map<String, Object>
     * @param response       HttpServletResponse
     * @return boolean
     */
    private void limitByFrequency(HandlerMethod handlerMethod, RequestStrategyParamsVo strategyParams, Map<String, Object> params, HttpServletResponse response) throws FrequencyAttrNameBlankException {
        List<FrequencyVo> frequencyVoList = getMethodFrequencies(handlerMethod, strategyParams.getMethodName(), strategyParams.getRequestUri());
        for(FrequencyVo frequency : frequencyVoList){
            frequencyConfigBiz.limitBySysconfigLoad(frequency, strategyParams.getCurTime());
            handleFrequency(response, params, frequency, strategyParams);
        }
    }

    private void limitByFrequencyAsync(HandlerMethod handlerMethod, RequestStrategyParamsVo strategyParams, Map<String, Object> params, HttpServletResponse response) throws FrequencyAttrNameBlankException {
        List<FrequencyVo> frequencyVoList = getMethodFrequencies(handlerMethod, strategyParams.getMethodName(), strategyParams.getRequestUri());
        List<CompletableFuture<Void>> futures = new ArrayList<>(frequencyVoList.size());
        List<Throwable> collectedExceptions = new ArrayList<>();
        for(FrequencyVo frequency : frequencyVoList){
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        frequencyConfigBiz.limitBySysconfigLoad(frequency, strategyParams.getCurTime());
                        handleFrequency(response, params, frequency, strategyParams);
                    }
            ).exceptionally(exception -> {
                if(exception!=null) {
                    collectedExceptions.add(exception);
                }
                return null;
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        CommUtils.throwOnAnyException(collectedExceptions);
    }

    private static Map<String, List<FrequencyVo>> methodFrequencyMap=new ConcurrentHashMap<>(100);
    private List<FrequencyVo> getMethodFrequencies(HandlerMethod handlerMethod, String method, String requestUri) {
        String code=handlerMethod.getBean().getClass().getSimpleName()+"/"+requestUri;
        List<FrequencyVo> list=methodFrequencyMap.get(code);
        if(list == null){
            list=new ArrayList<>();
            Frequencys frequencys = handlerMethod.getMethodAnnotation(Frequencys.class);
            if(frequencys!=null) {
                for (Frequency frequency : frequencys.value()) {
                    addFrequency(frequency, method, requestUri, list);
                }
            }
            Frequency frequency = handlerMethod.getMethodAnnotation(Frequency.class);
            if(frequency!=null){
                addFrequency(frequency, method, requestUri, list);
            }
            methodFrequencyMap.put(code, list);
        }
        return list;
    }

    private static void addFrequency(Frequency frequency, String method, String requestUri, List<FrequencyVo> list) {
        FrequencyVo frequencyVo = FrequencyVo.toFrequencyVo(frequency);
        frequencyVo.setRequestUri(requestUri);
        frequencyVo.setMethod(method);
        list.add(frequencyVo);
    }

    /**
     * 基于uri地址的频率限制
     *
     * @param requestVo      RequestVo
     * @param strategyParams RequestStrategyParamsVo
     * @param params         Map<String, Object>
     * @param response       HttpServletResponse
     */
    private void limitByUriConfig(RequestVo requestVo, RequestStrategyParamsVo strategyParams, Map<String, Object> params, HttpServletResponse response) throws FrequencyAttrNameBlankException {
        frequencyConfigBiz.limitBySysconfigUri(requestVo);
        List<LimitUriConfigVo> limitConfigList = requestVo.getLimitRequests();
        for (LimitUriConfigVo uriConfigVo : limitConfigList) {
            //无效数据不算
            if (uriConfigVo.getStatus() != 1) {
                continue;
            }
            FrequencyVo frequencyVo = FrequencyVo.toFrequencyVo(uriConfigVo);
            requestVo.setRequestUri(strategyParams.getRequestUri());
            frequencyVo.setSysconfig(true);
            handleFrequency(response, params, frequencyVo, strategyParams);
        }
    }

    private void limitByUriConfigAsync(RequestVo requestVo, RequestStrategyParamsVo strategyParams, Map<String, Object> params, HttpServletResponse response) throws FrequencyAttrNameBlankException {
        frequencyConfigBiz.limitBySysconfigUri(requestVo);
        List<LimitUriConfigVo> limitConfigList = requestVo.getLimitRequests();
        List<CompletableFuture<Void>> futures = new ArrayList<>(limitConfigList.size());
        List<Throwable> collectedExceptions = new ArrayList<>();
        for (LimitUriConfigVo uriConfigVo : limitConfigList) {
            //无效数据不算
            if (uriConfigVo.getStatus() != 1) {
                continue;
            }
            FrequencyVo frequencyVo = FrequencyVo.toFrequencyVo(uriConfigVo);
            requestVo.setRequestUri(strategyParams.getRequestUri());
            frequencyVo.setSysconfig(true);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                handleFrequency(response, params, frequencyVo, strategyParams);
                    }
            ).exceptionally(exception -> {
                if(exception!=null) {
                    collectedExceptions.add(exception);
                }
                return null;
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        CommUtils.throwOnAnyException(collectedExceptions);
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
        if (isNoLimit(handler)) {
            return;
        }
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
        //最大时长处理间隔
        final int runTimeInterval = frequencyConfiguration.getMaxRunTimeInterval() * FrequencyConstant.TIME_MILLISECOND_TO_SECOND;
        //接口调用时长记录
        requestRunTime(key, requestStartTime, runTimeInterval);
        //保存调用次数
        saveRunTaskCount(requestStartTime, runTimeInterval);
    }

    /**
     * 记录更新统计时间(用于减少调用次数)
     */
    private static long updateDateTime = 0L;

    /**
     * 通过单线程，每runTimeInterval，如30秒把执行次数更新到redis，缓存3天
     *
     * @param requestTime     请求时间
     * @param runTimeInterval 周期
     */
    private void saveRunTaskCount(long requestTime, final long runTimeInterval) {
        //判断是否需要落库
        if (requestTime - updateDateTime > runTimeInterval) {
            //更新刷新时间
            updateDateTime = requestTime;
            runCountTask.notifyRun();
        }
    }

    /**
     * 接口调用时长记录
     *
     * @param requestKey      uri+":"+method
     * @param requestTime     Long
     * @param runTimeInterval Long
     */
    private void requestRunTime(String requestKey, final long requestTime, final long runTimeInterval) {
        long runTime = System.currentTimeMillis() - requestTime;
        //记录超出最小执行时间的最大值
        if (runTime > frequencyConfiguration.getMinRunTime()) {
            RequestVo requestVo = requestVoMap.get(requestKey);
            //排除等一次请求
            if (requestVo != null && requestVo.getCounter().get() > 1) {
                //上次时间如果超过30秒，则清空，以便于重算
                if (requestVo.getMaxRunTimeCreateTime() < requestTime - runTimeInterval) {
                    requestVo.setMaxRunTime(0L);
                    requestVo.setMaxRunTimeCreateTime(0L);
                }
                if (requestVo.getMaxRunTime() < runTime) {
                    requestVo.setMaxRunTime(runTime);
                    requestVo.setMaxRunTimeCreateTime(requestTime);
                }
            }
        }
    }


    private boolean handleFrequency(HttpServletResponse response, Map<String, Object> params, FrequencyVo frequency, RequestStrategyParamsVo strategyParams) throws FrequencyAttrNameBlankException {
        boolean going = true;
        if (frequency == null) {
            return going;
        }
        long curTime = strategyParams.getCurTime();

        final int limit = frequency.getLimit();
        final int time = frequency.getTime();
        if (time == 0 || limit == 0) {
            log.warn("----handleFrequency--uri={} time={} limit={} invalid", strategyParams.getRequestUri(), time, limit);
            try {
                response.getWriter().println(FrequencyUtils.FREQUENCY_INVALID_INFO);
            } catch (IOException e) {
                log.warn("----handleFrequency--uri={} error={}", strategyParams.getRequestUri(), e.getMessage());
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        String userId = strategyParams.getUserId();
        String attrValue = null;
        //用于支持读取post的body中的userId或attrName数据
        if (userId == null || StringUtils.isNotBlank(frequency.getAttrName())) {
            attrValue = getAttrValue(params, frequency, strategyParams);
            userId = strategyParams.getUserId();
        }
        if (frequency.isNeedLogin()) {
            userBiz.checkUser(userId);
        }

        logFirstload(frequency, curTime);

        strategyParams.load(frequency, userId, attrValue);

        //这里的顺序没有关系，主要由frequency.limit.items这个参数来控制是否启用及顺序
        strategyParams.getChainOper().setPos(0);
        freqLimitChain.doCheckLimit(freqLimitChain, strategyParams);
        return going;
    }

    /**
     * 支持从post报文中获取参数(userId, attrName)
     *
     * @param params         请求参数map
     * @param frequency      频率限制配置
     * @param strategyParams 策略参数
     * @return
     */
    private String getAttrValue(Map<String, Object> params, FrequencyVo frequency, RequestStrategyParamsVo strategyParams) {
        String reqBody = (String) params.get(RequestUtils.REQ_BODYS);
        JSONObject reqObj = FrequencyUtils.getJsonObject(reqBody);
        String attrValue = null;
        if (reqObj != null) {
            //支持从post的报文中获取userId
            String userId = reqObj.getString(RequestParams.USER_ID);
            if (CharSequenceUtil.isNotBlank(userId)) {
                strategyParams.setUserId(userId);
            }
        }
        //支持从post报文中获取attrName对应的值
        if (StringUtils.isNotBlank(frequency.getAttrName())) {
            String attrName = frequency.getAttrName();
            attrValue = FrequencyUtils.getAttrNameValue(params, reqObj, attrName);
            //attrName的属性值为空，则不处理，由功能本身做参数验证
            if (StringUtils.isBlank(attrValue)) {
                log.warn("----handleFrequency={} attrName={} attrValue is null ignore limit, need checkNull on interface", frequency.getName(), attrName);
                throw new FrequencyAttrNameBlankException(attrName + " is blank,need checkNull on interface");
            }
        }
        return attrValue;
    }

    /**
     * 主要按接口缓存，理论上接口数不会太多，用Map做持久缓存，不会占太多内存
     */
    public static final Map<String, FrequencyVo> freqMap = new ConcurrentHashMap<>(64);
    public static final Map<String, RequestVo> requestVoMap = new ConcurrentHashMap<>(100);

    /**
     * 频纱限制配置，首次加载日志一下
     *
     * @param frequency FrequencyVo
     */
    private void logFirstload(FrequencyVo frequency, long curTime) {
        String typeInfo = frequency.getLimitType();
        if (!FrequencyType.URI_CONFIG.getType().equals(frequency.getLimitType())) {
            typeInfo += ":" + frequency.getFreqLimitType().getType();
        }
        String key = frequency.getName() + ":" + typeInfo + ":" + frequency.getTime();
        if (!freqMap.containsKey(key)) {
            frequency.setCreateTime(curTime);
            freqMap.put(key, frequency.toCopy());
            log.info("----logFirstload--redisPrefix={} name={} time={} limit={} freqLimitType={}", frequencyConfiguration.getRedisPrefix(), frequency.getName(), frequency.getTime(), frequency.getLimit(), frequency.getFreqLimitType());
        }
    }

    private RequestVo logFirstLoadRequest(HandlerMethod handlerMethod, String method, RequestStrategyParamsVo strategyParams) {
        String key = strategyParams.getRequestUri() + "." + method;
        requestKey.set(key);
        RequestVo request = requestVoMap.computeIfAbsent(key, k->{
            RequestVo requestVo = new RequestVo();
            requestVo.setCounter(new AtomicInteger());
            requestVo.setLimitCounter(new AtomicInteger());
            requestVo.setRequestUri(strategyParams.getRequestUri());
            requestVo.setBeanName(handlerMethod.getBeanType().getSimpleName());
            requestVo.setMethod(method);
            requestVo.setMethodName(handlerMethod.getMethod().getName());
            requestVo.setCreateTime(strategyParams.getCurTime());
            return requestVo;
        });
        request.getCounter().incrementAndGet();
        return request;
    }

    private boolean isNoLimit(Object handler) {
        return !(handler instanceof HandlerMethod);
    }
}
