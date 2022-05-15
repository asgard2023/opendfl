package org.ccs.opendfl.core.limitfrequency;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.ccs.opendfl.core.biz.IOutLogBiz;
import org.ccs.opendfl.core.biz.IRsaBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
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


    /**
     * 频率超限日志，超出部分才记录
     *
     * @param strategyParams
     * @param limit          limitCount, use when not null
     * @param type           limit type
     */
    public static void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, long v, FreqLimitType type) {
        outLimitCount(strategyParams, type);
        outLogBiz.addFreqLog(strategyParams, limit, v, type);
        Integer logTime = frequencyConfiguration.getLimit().getOutLimitLogTime();
        FrequencyVo frequency = strategyParams.getFrequency();
        if (frequency==null || !(logTime > 0 && frequency.getTime() >= logTime)) {
            return;
        }
        String uri = strategyParams.getRequestUri();
        String ip = strategyParams.getIp();
        String userId = strategyParams.getUserId();
        Integer countLimit = frequency.getLimit();
        if (limit != null) {
            countLimit = limit;
        }
        logger.info("----addFreqLog--type={} uri={} limit={} attrData={} reqCount={} ip={}", type.getCode(), uri, countLimit, userId, v, ip);
    }

    public static final Map<String, Map<String, AtomicInteger>> outLimitCountMap = new ConcurrentHashMap<>();

    public static void outLimitCount(RequestStrategyParamsVo strategyParams, String limitType) {
        FreqLimitType freqLimitType = FreqLimitType.parseCode(limitType);
        if (freqLimitType != null) {
            outLimitCount(strategyParams, freqLimitType);
        }
    }

    /**
     * 按接口记录超限次数
     *
     * @param strategyParams RequestStrategyParamsVo
     * @param type           FreqLimitType
     */
    public static void outLimitCount(RequestStrategyParamsVo strategyParams, FreqLimitType type) {
        if (frequencyConfiguration.getRunCountCacheDay().intValue() == 0) {
            return;
        }
        Map<String, AtomicInteger> typeCountMap = outLimitCountMap.get(type.getCode());
        if (typeCountMap == null) {
            typeCountMap = new ConcurrentHashMap<>();
            outLimitCountMap.put(type.getCode(), typeCountMap);
        }
        AtomicInteger counter = typeCountMap.get(strategyParams.getRequestUri());
        if (counter == null) {
            counter = new AtomicInteger();
            typeCountMap.put(strategyParams.getRequestUri(), counter);
        }
        counter.incrementAndGet();
    }


    public static boolean failExceptionMsg(String title, String errMsg, String errMsgEn, String lang) {
        boolean isCn = LangType.ZH.code.equals(lang);
        title = "frequency:" + title;
        BaseException exception = null;
        if (StringUtils.isBlank(errMsg)) {
            if (isCn) {
                exception = new FrequencyException();
            } else {
                exception = new FrequencyException("Frequency limit");
            }
        } else {
            if (isCn && StringUtils.isNotBlank(errMsg)) {
                exception = new FrequencyException("访问频率限制" + ":" + errMsg);
            } else if (StringUtils.isNotBlank(errMsgEn)) {
                exception = new FrequencyException("Frequency limit" + ":" + errMsgEn);
            } else {
                exception = new FrequencyException(ResultCode.USER_FREQUENCY_ERROR.getMsg() + ":" + errMsg);
            }
        }
        exception.setTitle(title);
        throw exception;
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


    public static String getAttrNameValue(Map<String, Object> params, String attrName) {
        Object attrValue = params.get(attrName);
        String clientIdRsa = (String) params.get("clientIdRsa");

        if (attrValue == null) {
            String reqBody = (String) params.get(RequestUtils.REQ_BODYS);
            if (reqBody != null) {
                JSONObject jsonObject = JSON.parseObject(reqBody);
                attrValue = jsonObject.getString(attrName);
                if (clientIdRsa == null) {
                    clientIdRsa = jsonObject.getString(clientIdRsa);
                }
            }
        }
        if (attrValue == null) {
            return null;
        }
        return decryptValue(clientIdRsa, "" + attrValue);
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
}
