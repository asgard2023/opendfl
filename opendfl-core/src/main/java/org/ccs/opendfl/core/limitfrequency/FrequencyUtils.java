package org.ccs.opendfl.core.limitfrequency;


import org.ccs.opendfl.core.biz.IUserBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.exception.FrequencyException;
import org.ccs.opendfl.core.exception.ResultCode;
import org.ccs.opendfl.core.utils.LangType;
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

@Component
public class FrequencyUtils {
    private static Logger logger = LoggerFactory.getLogger(FrequencyUtils.class);

    private FrequencyUtils() {

    }


    private static IUserBiz userBiz;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setUserBiz(IUserBiz userBiz) {
        FrequencyUtils.userBiz = userBiz;
    }

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FrequencyUtils.frequencyConfiguration = frequencyConfiguration;
    }


    /**
     * 频率超限日志，超出部分才记录
     *
     * @param strategyParams
     * @param limit
     * @param curTime
     * @param v
     * @param type
     */
    public static void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, Long curTime, long v, FreqLimitType type) {
        Integer logTime = frequencyConfiguration.getLimit().getOutLimitLogTime();
        FrequencyVo frequency = strategyParams.getFrequency();
        if (!(logTime > 0 && frequency.getTime() >= logTime)) {
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



    public static boolean failExceptionMsg(String errMsg, String errMsgEn, String lang) {
        boolean isCn = LangType.ZH.code.equals(lang);
        boolean isJa = LangType.JA.code.equals(lang);
        if (StringUtils.isEmpty(errMsg)) {
            if (isJa) {
                throw new FrequencyException("周波数制限");
            }
            if (!isCn) {
                throw new FrequencyException("Frequency limit");
            }
            throw new FrequencyException();
        } else {
            if (isJa) {
                throw new FrequencyException("周波数制限" + ":" + errMsgEn);
            }
            if (!isCn && StringUtils.isNotBlank(errMsgEn)) {
                throw new FrequencyException("Frequency limit" + ":" + errMsgEn);
            }
            throw new FrequencyException(ResultCode.USER_FREQUENCY_ERROR_MSG + ":" + errMsg);
        }
    }

    protected static final Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();
    protected static final Map<String, Boolean> initLogrMap = new ConcurrentHashMap<>();

    /**
     * 是否启动时前1000次
     * 以便于显示日志信息
     *
     * @param key
     * @return
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
            Integer count = counter.incrementAndGet();
            if (count > frequencyConfiguration.getInitLogCount()) {
                isLog = false;
                initLogrMap.put(key, isLog);
            }
        }
        return isLog;
    }
}
