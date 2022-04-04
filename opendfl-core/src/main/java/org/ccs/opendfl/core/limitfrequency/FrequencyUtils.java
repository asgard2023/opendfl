package org.ccs.opendfl.core.limitfrequency;


import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.RequestLockConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.exception.BaseException;
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

/**
 * @author chenjh
 */
@Component
public class FrequencyUtils {
    private static final Logger logger = LoggerFactory.getLogger(FrequencyUtils.class);

    private FrequencyUtils() {

    }


    private static FrequencyConfiguration frequencyConfiguration;
    private static RequestLockConfiguration requestLockConfiguration;

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FrequencyUtils.frequencyConfiguration = frequencyConfiguration;
    }

    @Autowired
    public void setRequestLockConfiguration(RequestLockConfiguration requestLockConfiguration) {
        FrequencyUtils.requestLockConfiguration = requestLockConfiguration;
    }

    public static String getRedisKeyLock(String lockName, String dataId) {
        return requestLockConfiguration.getRedisPrefix() + ":" + lockName + ":" + dataId;
    }


    /**
     * 频率超限日志，超出部分才记录
     *
     * @param strategyParams
     * @param limit          limitCount, use when not null
     * @param type           limit type
     */
    public static void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, long v, FreqLimitType type) {
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
                exception = new FrequencyException(ResultCode.USER_FREQUENCY_ERROR_MSG + ":" + errMsg);
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
}