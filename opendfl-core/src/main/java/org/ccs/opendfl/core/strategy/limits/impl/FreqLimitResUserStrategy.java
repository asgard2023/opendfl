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
 * 同资源ID同用户访问次数限制
 *
 * @author chenjh
 */
@Service(value = "freqLimitResUserStrategy")
public class FreqLimitResUserStrategy implements FreqLimitStrategy {
    private static final Logger logger = LoggerFactory.getLogger(FreqLimitResUserStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.RES_USER;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FreqLimitResUserStrategy.frequencyConfiguration = frequencyConfiguration;
    }

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    public static String getRedisKey(FrequencyVo frequency, String userId, String attrValue) {
        frequency.setFreqLimitType(LIMIT_TYPE);
        return FrequencyUtils.getRedisKey(frequency, userId+":"+attrValue);
    }

    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams) {
        FrequencyVo frequency = strategyParams.getFrequency();
        final int limit = frequency.getLimit();
        if (LIMIT_TYPE == frequency.getFreqLimitType() && limit > 0 && containLimit(limitItems, LIMIT_TYPE)) {
            String userId = strategyParams.getUserId();
            if (StringUtils.isBlank(userId)) {
                if(frequency.isLog()) {
                    logger.warn("requestUri={} time={} userId is blank, ignore limit, need checkNull userId on interface", frequency.getRequestUri(), frequency.getTime());
                }
            }
            else {
                checkLimit(strategyParams, frequency, limit);
            }
        }
        limitChain.doCheckLimit(limitChain, strategyParams);
    }

    private void checkLimit(RequestStrategyParamsVo strategyParams, FrequencyVo frequency, int limit) {
        String userId = strategyParams.getUserId();
        String lang = strategyParams.getLang();
        String ip = strategyParams.getIp();
        String redisKey = getRedisKey(frequency, userId, strategyParams.getAttrValue());
        long v = redisTemplate.opsForValue().increment(redisKey, 1);
        if(frequency.isLog()) {
            logger.info("----resUser--redisKey={} limit={} v={}", redisKey, limit, v);
        }
        final int time = frequency.getTime();
        if (v == 1) {
            redisTemplate.expire(redisKey, time, TimeUnit.SECONDS);
        } else {
            //主要用于避免服务重启造成部份key变成永久key
            if (v < limit) {
                RedisTemplateUtil.expireTimeHashFrequencyCache(redisTemplate, redisKey, time, v);
            } else if (v > limit) {
                strategyParams.getChainOper().setFail(true);
                //再次过期处理，以免有变成永久的key
                RedisTemplateUtil.expireTimeTTL(redisTemplate, redisKey, frequency.getTime());
                logger.warn("----doCheckLimit-resUser--redisKey={} userId={} time={} count={} limit={} ip={}", redisKey, userId, time, v, limit, ip);

                FrequencyUtils.addFreqLog(strategyParams, limit, v, LIMIT_TYPE);
                FrequencyUtils.failExceptionMsg(getLimitType(), frequency, lang);
            }
        }
    }
}
