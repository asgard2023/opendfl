package org.ccs.opendfl.core.strategy.limits.impl;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.limitcount.FrequencyUtils;
import org.ccs.opendfl.core.limitcount.FrequencyWhiteCodeUtils;
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
 * 用户访问频率检查
 */
@Service(value = "freqLimitUserCountStrategy")
public class FreqLimitUserCountStrategy implements FreqLimitStrategy {
    private static Logger logger = LoggerFactory.getLogger(FreqLimitUserCountStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.LIMIT;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setLimitStrategyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FreqLimitUserCountStrategy.frequencyConfiguration = frequencyConfiguration;
    }

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    public static String getRedisKey(FrequencyVo frequency, String dataId, String ip) {
        final String redisKey = frequencyConfiguration.getRedisPrefix();
        String key = redisKey + ":" + frequency.getName() + ":" + frequency.getTime();
        if (dataId == null) {
            key += ":noUser:" + ip;
        } else {
            key += ":" + dataId;
        }
        return key;
    }

    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            RequestStrategyParamsVo strategyParams = limitChain.getStrategyParams();
            FrequencyVo frequency = strategyParams.getFrequency();
            int time = frequency.getTime();
            String redisKey = getRedisKey(frequency, strategyParams.getUserId(), strategyParams.getIp());
            long v = redisTemplate.opsForValue().increment(redisKey, 1);
            int limit = frequency.getLimit();
            if (v == 1) {
                redisTemplate.expire(redisKey, frequency.getTime(), TimeUnit.SECONDS);
            } else {
                if (v > limit) {
                    String userId = strategyParams.getUserId();
                    String lang = strategyParams.getLang();
                    String ip = strategyParams.getIp();
                    //再次过期处理，以免有变成永久的key
                    RedisTemplateUtil.expireTimeTTL(redisTemplate, redisKey, frequency.getTime());
                    if (FrequencyWhiteCodeUtils.checkWhiteUserId(frequency, strategyParams.getCurTime(), userId)) {
                        logger.info("----doCheckLimit-userCount--whiteUserId-redisKey={} time={} count={} limit={} ip={}", redisKey, time, v, limit, ip);
                        //如果是功能白名单直接结束策略，不用往下执行
                        return;
                    }
                    logger.warn("----doCheckLimit-userCount--redisKey={} lang={} time={} count={} limit={} ip={}", redisKey, lang, time, v, limit, ip);

                    FrequencyUtils.addFreqLog(strategyParams, limit, strategyParams.getCurTime(), v, LIMIT_TYPE);
                    final String errMsg = String.format(frequency.getErrMsg(), frequency.getLimit());
                    final String errMsgEn = String.format(frequency.getErrMsgEn(), frequency.getLimit());
                    FrequencyUtils.failExceptionMsg(errMsg, errMsgEn, lang);
                }
            }
        }
        limitChain.doCheckLimit(limitChain);
    }


}
