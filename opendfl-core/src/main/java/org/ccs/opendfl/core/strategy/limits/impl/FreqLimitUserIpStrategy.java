package org.ccs.opendfl.core.strategy.limits.impl;

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
 * 同用户IP数检查
 *
 * @author chenjh
 */
@Service(value = "freqLimitUserIpStrategy")
public class FreqLimitUserIpStrategy implements FreqLimitStrategy {
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.USER_IP;
    private static final Logger logger = LoggerFactory.getLogger(FreqLimitUserIpStrategy.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    public static String getRedisKey(FrequencyVo frequency, String userId) {
        return FrequencyUtils.getRedisKey(frequency, LIMIT_TYPE, userId);
    }

    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams) {
        FrequencyVo frequency = strategyParams.getFrequency();
        int limit = frequency.getLimit();
        if (LIMIT_TYPE == frequency.getFreqLimitType() && limit > 0 && containLimit(limitItems, LIMIT_TYPE)) {
            String userId = strategyParams.getUserId();
            if (StringUtils.isBlank(userId)) {
                if(frequency.isLog()) {
                    logger.warn("requestUri={} time={} userId is blank, ignore limit, need checkNull userId on interface", frequency.getRequestUri(), frequency.getTime());
                }
            }
            else {
                checkLimit(strategyParams, frequency, limit, userId);
            }
        }

        limitChain.doCheckLimit(limitChain, strategyParams);
    }

    private void checkLimit(RequestStrategyParamsVo strategyParams, FrequencyVo frequency, int limit, String userId) {
        String lang = strategyParams.getLang();
        String ip = strategyParams.getIp();
        String redisKey = getRedisKey(frequency, userId);
        long v = redisTemplate.opsForSet().size(redisKey);
        if(frequency.isLog()) {
            logger.info("----userIp--redisKey={} limit={} v={}", redisKey, limit, v);
        }
        if (v < limit + FreqLimitType.REDIS_SET_OUT_LIMIT) {
            v += redisTemplate.opsForSet().add(redisKey, ip);
            if (v == 1) {
                redisTemplate.expire(redisKey, frequency.getTime(), TimeUnit.SECONDS);
            }
        }
        if (v > limit) {
            //再次过期处理，以免有变成永久的key
            RedisTemplateUtil.expireTimeTTL(redisTemplate, redisKey, frequency.getTime());
            final int time = frequency.getTime();
            logger.warn("----doCheckLimit-userIp--redisKey={} userId={} time={} count={} limit={} ip={}", redisKey, userId, time, v, limit, ip);

            FrequencyUtils.addFreqLog(strategyParams, limit, v, LIMIT_TYPE);
            FrequencyUtils.failExceptionMsg(getLimitType(), frequency, lang);
        }
    }
}
