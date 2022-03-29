package org.ccs.opendfl.core.strategy.limits.impl;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.limitcount.FrequencyUtils;
import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.strategy.limits.FreqLimitStrategy;
import org.ccs.opendfl.core.utils.RedisTemplateUtil;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * IP限限制检查
 */
@Service(value = "freqLimitIpStrategy")
public class FreqLimitIpStrategy implements FreqLimitStrategy {
    private static Logger logger = LoggerFactory.getLogger(FreqLimitIpStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.LIMIT_IP;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setLimitStrategyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FreqLimitIpStrategy.frequencyConfiguration = frequencyConfiguration;
    }


    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    public String getRedisKey(FrequencyVo frequency, String ip) {
        return frequencyConfiguration.getRedisPrefix() + ":" + LIMIT_TYPE.getCode() + ":" + frequency.getName() + ":" + frequency.getTime() + ":" + ip;
    }

    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            RequestStrategyParamsVo strategyParams = limitChain.getStrategyParams();
            FrequencyVo frequency = strategyParams.getFrequency();
            String ip = strategyParams.getIp();
            String redisKey = getRedisKey(frequency, ip);
            int limit = frequency.getLimit() * 5;
            int time = frequency.getTime();
            long v = redisTemplate.opsForValue().increment(redisKey, 1);
            if (v == 1) {
                redisTemplate.expire(redisKey, frequency.getTime(), TimeUnit.SECONDS);
            } else if (v > limit) {
                String userId = strategyParams.getUserId();
                String lang = strategyParams.getLang();

                //再次过期处理，以免有变成永久的key
                RedisTemplateUtil.expireTimeTTL(redisTemplate, redisKey, frequency.getTime());
                final String errMsg = String.format(frequency.getErrMsg(), frequency.getLimit());
                final String errMsgEn = String.format(frequency.getErrMsgEn(), frequency.getLimit());

                logger.warn("----doCheckLimit-limitIp--redisKey={} userId={} time={} count={} limit={} ip={}", redisKey, userId, time, v, limit, ip);


                FrequencyUtils.addFreqLog(strategyParams, limit, strategyParams.getCurTime(), v, LIMIT_TYPE);
                FrequencyUtils.failExceptionMsg(errMsg, errMsgEn, lang);
            }
        }
        limitChain.doCheckLimit(limitChain);
    }
}
