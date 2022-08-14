package org.ccs.opendfl.core.limitfrequency;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.ccs.opendfl.core.biz.IOutLogBiz;
import org.ccs.opendfl.core.biz.IRsaBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.OutLimitType;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FrequencyException;
import org.ccs.opendfl.core.exception.ResultCode;
import org.ccs.opendfl.core.utils.LangType;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenjh
 */
@Component
public class FrequencyUtils {
    private static final Logger logger = LoggerFactory.getLogger(FrequencyUtils.class);

    private FrequencyUtils() {

    }

    private static IRsaBiz rsaBiz;
    private static IOutLogBiz outLogBiz;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FrequencyUtils.frequencyConfiguration = frequencyConfiguration;
    }


    @Autowired
    public void setRsaBiz(IRsaBiz rsaBiz) {
        FrequencyUtils.rsaBiz = rsaBiz;
    }

    @Autowired
    public void setOutLogBiz(IOutLogBiz outLogBiz) {
        FrequencyUtils.outLogBiz = outLogBiz;
    }

    public static void outLogBiz(IOutLogBiz outLogBiz) {
        FrequencyUtils.outLogBiz = outLogBiz;
    }


    public static void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, long v, OutLimitType outType, WhiteBlackCheckType type) {
        if (type == null) {
            return;
        }
        addFreqLog(strategyParams, limit, v, outType, null, type.getCode());
    }

    /**
     * 频率超限日志，超出部分才记录
     *
     * @param strategyParams
     * @param limit          limitCount, use when not null
     * @param type           limit type
     */
    public static void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, long v, FreqLimitType type) {

        OutLimitType outLimitType = OutLimitType.FREQUENCY;
        addFreqLog(strategyParams, limit, v, outLimitType, strategyParams.getFrequency().getLimitType(), type.getCode());
    }

    private static void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, long v, OutLimitType outLimitType, String subLimit, String typeCode) {
        outLimitCount(strategyParams, typeCode);
        FrequencyVo frequencyVo = strategyParams.getFrequency();
        Integer ifResource = 0;
        if (frequencyVo!=null && frequencyVo.getFreqLimitType().isResource()) {
            ifResource = 1;
        }
        outLogBiz.addFreqLog(strategyParams, limit, v, outLimitType, subLimit, ifResource, typeCode, strategyParams.getAttrValue());

        Integer logTime = frequencyConfiguration.getLimit().getOutLimitLogTime();
        FrequencyVo frequency = strategyParams.getFrequency();
        if (frequency == null || !(logTime > 0 && frequency.getTime() >= logTime)) {
            return;
        }

        String uri = strategyParams.getRequestUri();
        String ip = strategyParams.getIp();
        String userId = strategyParams.getUserId();
        Integer countLimit = frequency.getLimit();
        if (limit != null) {
            countLimit = limit;
        }
        logger.info("----addFreqLog--type={} uri={} limit={} attrData={} reqCount={} ip={}", typeCode, uri, countLimit, userId, v, ip);
    }

    public static final Map<String, Map<String, AtomicInteger>> outLimitCountMap = new ConcurrentHashMap<>();

    public static void outLimitCount(RequestStrategyParamsVo strategyParams, WhiteBlackCheckType type) {
        outLimitCount(strategyParams, type.getCode());
    }

    /**
     * 按接口记录超限次数
     *
     * @param strategyParams RequestStrategyParamsVo
     * @param type           FreqLimitType
     */
    public static void outLimitCount(RequestStrategyParamsVo strategyParams, FreqLimitType type) {
        outLimitCount(strategyParams, type.getCode());
    }

    private static void outLimitCount(RequestStrategyParamsVo strategyParams, String typeCode) {
        if (frequencyConfiguration.getRunCountCacheDay().intValue() == 0) {
            return;
        }
        Map<String, AtomicInteger> typeCountMap = outLimitCountMap.get(typeCode);
        if (typeCountMap == null) {
            typeCountMap = new ConcurrentHashMap<>();
            outLimitCountMap.put(typeCode, typeCountMap);
        }
        AtomicInteger counter = typeCountMap.get(strategyParams.getRequestUri());
        if (counter == null) {
            counter = new AtomicInteger();
            typeCountMap.put(strategyParams.getRequestUri(), counter);
        }
        counter.incrementAndGet();
    }


    protected static final Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();
    protected static final Map<String, Boolean> initLogrMap = new ConcurrentHashMap<>();

    /**
     * 是否启动时前1000次
     * 以便于显示日志信息
     *
     * @param key
     */
    public static boolean isInitLog(String key) {
        Boolean isLog = initLogrMap.get(key);
        if (isLog == null) {
            isLog = true;
            initLogrMap.put(key, isLog);
        }
        AtomicInteger counter = counterMap.get(key);
        if (counter == null) {
            counter = new AtomicInteger();
            counterMap.put(key, counter);
        }
        if (isLog) {
            int count = counter.incrementAndGet();
            if (count > frequencyConfiguration.getInitLogCount()) {
                isLog = false;
                initLogrMap.put(key, isLog);
            }
        }
        return isLog;
    }


    public static String getAttrNameValue(Map<String, Object> params, JSONObject jsonObject, String attrName) {
        Object attrValue = params.get(attrName);
        String clientIdRsa = (String) params.get("clientIdRsa");

        if (attrValue == null && jsonObject!=null) {
            attrValue = jsonObject.getString(attrName);
            if (clientIdRsa == null) {
                clientIdRsa = jsonObject.getString(clientIdRsa);
            }
        }
        if (attrValue == null) {
            return null;
        }
        return decryptValue(clientIdRsa, "" + attrValue);
    }

    public static JSONObject getJsonObject(String reqBody){
        if (reqBody!=null && reqBody.startsWith("{")) {
            return JSON.parseObject(reqBody);
        }
        return null;
    }



    /**
     * 用于支持加密的数据解密，比如登入账号，否则不好限制
     *
     * @param clientIdRsa
     * @param attrValue
     * @return decryptValue
     */
    private static String decryptValue(String clientIdRsa, String attrValue) {
        if (attrValue != null && StringUtils.isNotBlank(clientIdRsa)) {
            attrValue = rsaBiz.checkRSAKey(clientIdRsa, attrValue);
        }
        return attrValue;
    }

    public static String getErrMsg(String errMsg, int time, int limit){
        if(errMsg==null){
            return null;
        }
        try {
            return errMsg.replace("#{time}", ""+time).replace("#{limit}", ""+limit);
        } catch (Exception e) {
            return errMsg;
        }
    }

    public static String getErrMsg(FrequencyVo frequency, LangType langType){
        if(frequency==null){
            return null;
        }
        if(langType==LangType.EN){
            return getErrMsg(frequency.getErrMsgEn(), frequency.getTime(), frequency.getLimit());
        }
        return getErrMsg(frequency.getErrMsg(), frequency.getTime(), frequency.getLimit());
    }

    public static boolean failExceptionMsg(String title, FrequencyVo frequency, String lang) {
//        boolean isCn = LangType.ZH.code.equals(lang);
        title = "frequency:" + title;
        LangType langType = LangType.parse(lang);
        String limitInfo = getLimitInfo(langType);
        if(frequency==null){
            BaseException exception = new FrequencyException(limitInfo);
            exception.setTitle(title);
            throw exception;
        }

        String errMsg=getErrMsg(frequency, langType);
        BaseException exception = new FrequencyException(limitInfo+ ":" + errMsg);
        exception.setTitle(title);
        throw exception;
    }

    private static String getLimitInfo(LangType langType) {
        String limitInfo;
        if(LangType.ZH== langType){
            limitInfo = "访问频率限制";
        }
        else{
            limitInfo = "Frequency limit";
        }
        return limitInfo;
    }

    public static String getFailErrMsg(OutLimitType limitType, String title, FrequencyVo frequency, String lang){
        LangType langType = LangType.parse(lang);
        String errMsg = getLimitInfo(langType);
        if(frequency!=null){
            errMsg=FrequencyUtils.getErrMsg(frequency, langType);
        }

        String limitCode="100030";
        if(OutLimitType.BLACK==limitType){
            limitCode= ResultCode.USER_BLACK_ERROR.getCode();
        }
        else if(OutLimitType.WHITE==limitType){
            limitCode= ResultCode.USER_WHITE_ERROR.getCode();
        }
        return String.format(FrequencyUtils.OUT_INFO, limitCode, errMsg, title);
    }

    public static final String FREQUENCY_INVALID_INFO = "{\"resultCode\":\"100030\",\"errorMsg\":\"Frequency limit\",\"errorType\":\"time=0 or limit=0\",\"success\":false}";
    public static final String OUT_INFO = "{\"resultCode\":\"%s\",\"errorMsg\":\"%s\",\"errorType\":\"%s\",\"success\":false}";

    /**
     * key拼接：pedisPrefix + ":" + LIMIT_TYPE.getType() + ":" + name + ":" + time
     * @param frequency
     * @return
     */
    public static StringBuilder getRedisKeyBase(FrequencyVo frequency){
        StringBuilder sb = new StringBuilder();
        sb.append(frequencyConfiguration.getRedisPrefix());
        sb.append("e:");
        sb.append(frequency.getFreqLimitType().getType());
        sb.append(":");
        sb.append(frequency.getName());
        sb.append(":");
        sb.append(frequency.getTime());
        return sb;
    }
}
