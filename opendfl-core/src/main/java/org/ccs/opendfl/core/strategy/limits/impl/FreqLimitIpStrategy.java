package org.ccs.opendfl.core.strategy.limits.impl;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
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
 * IP限限制检查，即同IP访问次数检查
 * limitIp的限制数=limit*2
 *
 * @author chenjh
 */
@Service(value = "freqLimitIpStrategy")
public class FreqLimitIpStrategy implements FreqLimitStrategy {
    private static final Logger logger = LoggerFactory.getLogger(FreqLimitIpStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.LIMIT_IP;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FreqLimitIpStrategy.frequencyConfiguration = frequencyConfiguration;
    }


    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    public static String getRedisKey(FrequencyVo frequency, String ip) {
        StringBuilder sb = FrequencyUtils.getRedisKeyBase(frequency);
        sb.append(":");
        sb.append(ip);
        return sb.toString();
    }

    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams) {
        FrequencyVo frequency = strategyParams.getFrequency();
        if (LIMIT_TYPE == frequency.getFreqLimitType() && containLimit(limitItems, LIMIT_TYPE)) {
            String ip = strategyParams.getIp();
            Float limitFloat = frequency.getLimit() * frequencyConfiguration.getLimitIpRate();
            int limit = limitFloat.intValue();
            String redisKey = getRedisKey(frequency, ip);
            int time = frequency.getTime();
            long v = redisTemplate.opsForValue().increment(redisKey, 1);
            if(frequency.isLog()) {
                logger.info("----limitIp--redisKey={} limit={} v={}", redisKey, limit, v);
            }
            if (v == 1) {
                redisTemplate.expire(redisKey, frequency.getTime(), TimeUnit.SECONDS);
            } else {
                //主要用于避免服务重启造成部份key变成永久key
                //低于60秒的也忽略，就让用户多等一下，否则就检查一下是否永久key
                if (v < limit) {
                    RedisTemplateUtil.expireTimeHashFrequencyCache(redisTemplate, redisKey, time, v);
                } else if (v > limit) {
                    strategyParams.getChainOper().setFail(true);
                    String userId = strategyParams.getUserId();
                    String lang = strategyParams.getLang();

                    //再次过期处理，以免有变成永久的key
                    RedisTemplateUtil.expireTimeTTL(redisTemplate, redisKey, frequency.getTime());

                    logger.warn("----doCheckLimit-limitIp--redisKey={} userId={} time={} count={} limit={} ip={}", redisKey, userId, time, v, limit, ip);

                    FrequencyUtils.addFreqLog(strategyParams, limit, v, LIMIT_TYPE);
                    FrequencyUtils.failExceptionMsg(getLimitType(), frequency, lang);
                }
            }
        }
        limitChain.doCheckLimit(limitChain, strategyParams);
    }
}
