package org.ccs.opendfl.core.strategy.limits.impl;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.strategy.limits.FreqLimitStrategy;
import org.ccs.opendfl.core.utils.RedisTemplateUtil;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * IP用户数检查
 *
 * @author chenjh
 */
@Service(value = "freqLimitIpUserStrategy")
public class FreqLimitIpUserStrategy implements FreqLimitStrategy {
    private static final Logger logger = LoggerFactory.getLogger(FreqLimitIpUserStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.IP_USER_COUNT;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FreqLimitIpUserStrategy.frequencyConfiguration = frequencyConfiguration;
    }

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    public static String getRedisKey(FrequencyVo frequency, String ip) {
        String function = "";
        if (StringUtils.ifYes(frequencyConfiguration.getLimit().getIpLimitSplitFunction())) {
            function = ":" + frequency.getName();
        }
        return frequencyConfiguration.getRedisPrefix() + ":" + LIMIT_TYPE.getCode() + function + ":" + frequency.getTime() + ":" + ip;
    }

    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            FrequencyVo frequency = strategyParams.getFrequency();
            if(!LIMIT_TYPE.isResource() && frequency.isResource()){
                limitChain.doCheckLimit(limitChain, strategyParams);
                return;
            }
            int limit = frequency.getIpUserCount();
            if (limit > 0) {
                String userId = strategyParams.getUserId();
                String lang = strategyParams.getLang();
                String ip = strategyParams.getIp();
                String redisKey = getRedisKey(frequency, ip);
                long v = redisTemplate.opsForSet().size(redisKey);
                if (v < limit + FreqLimitType.REDIS_SET_OUT_LIMIT) {
                    v += redisTemplate.opsForSet().add(redisKey, userId);
                    if (v == 1) {
                        redisTemplate.expire(redisKey, frequency.getTime(), TimeUnit.SECONDS);
                    }
                }
                if (v > limit) {
                    strategyParams.getChainOper().setFail(true);
                    //再次过期处理，以免有变成永久的key
                    RedisTemplateUtil.expireTimeTTL(redisTemplate, redisKey, frequency.getTime());
                    final int time = frequency.getTime();
                    logger.warn("----doCheckLimit-ipUser--redisKey={} userId={} time={} count={} limit={} ip={}", redisKey, userId, time, v, limit, ip);

                    FrequencyUtils.addFreqLog(strategyParams, limit, v, LIMIT_TYPE);
                    FrequencyUtils.failExceptionMsg(getLimitType(), frequency, lang);
                }
            }
        }
        limitChain.doCheckLimit(limitChain, strategyParams);
    }
}
