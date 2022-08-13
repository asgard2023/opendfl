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
 * 同资源ID同IP访问次数限制
 *
 * @author chenjh
 */
@Service(value = "freqLimitResIpStrategy")
public class FreqLimitResIpStrategy implements FreqLimitStrategy {
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.RES_IP;
    private static final Logger logger = LoggerFactory.getLogger(FreqLimitResIpStrategy.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FreqLimitResIpStrategy.frequencyConfiguration = frequencyConfiguration;
    }


    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    public static String getRedisKey(FrequencyVo frequency, String ip, String attrValue) {
        return frequencyConfiguration.getRedisPrefix() + ":" + LIMIT_TYPE.getType() + ":" + frequency.getName() + ":" + frequency.getTime() + ":" + ip + ":" + attrValue;
    }

    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams) {
        FrequencyVo frequency = strategyParams.getFrequency();
        if (LIMIT_TYPE == frequency.getFreqLimitType() && containLimit(limitItems, LIMIT_TYPE)) {
            int limit = frequency.getLimit();
            if (limit > 0) {
                String userId = strategyParams.getUserId();
                String lang = strategyParams.getLang();
                String ip = strategyParams.getIp();
                String redisKey = getRedisKey(frequency, ip, strategyParams.getAttrValue());
                long v = redisTemplate.opsForValue().increment(redisKey, 1);
                final int time = frequency.getTime();
//                logger.info("----resIp--redisKey={} limit={} v={}", redisKey, limit, v);
                if (v == 1) {
                    redisTemplate.expire(redisKey, time, TimeUnit.SECONDS);
                } else {
                    //主要用于避免服务重启造成部份key变成永久key
                    //低于60秒的也忽略，就让用户多等一下，否则就检查一下是否永久key
                    if (v < limit) {
                        RedisTemplateUtil.expireTimeHashFrequencyCache(redisTemplate, redisKey, time, v);
                    } else if (v > limit) {
                        //再次过期处理，以免有变成永久的key
                        RedisTemplateUtil.expireTimeTTL(redisTemplate, redisKey, frequency.getTime());
                        logger.warn("----doCheckLimit-resIp--redisKey={} userId={} time={} count={} limit={} ip={}", redisKey, userId, time, v, limit, ip);

                        FrequencyUtils.addFreqLog(strategyParams, limit, v, LIMIT_TYPE);
                        FrequencyUtils.failExceptionMsg(getLimitType(), frequency, lang);
                    }
                }
            }
        }
        limitChain.doCheckLimit(limitChain, strategyParams);
    }
}
