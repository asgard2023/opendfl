package org.ccs.opendfl.core.strategy.limits.impl;

import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
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
 * 用户访问频率检查，同用户访问次数检查
 *
 * @author chenjh
 */
@Service(value = "freqLimitUserCountStrategy")
public class FreqLimitUserCountStrategy implements FreqLimitStrategy {
    private static final Logger logger = LoggerFactory.getLogger(FreqLimitUserCountStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.LIMIT;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IWhiteBlackCheckBiz whiteBlackCheckBiz;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FreqLimitUserCountStrategy.frequencyConfiguration = frequencyConfiguration;
    }

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    public static String getRedisKey(FrequencyVo frequency, String userId, String ip) {
        String key = frequencyConfiguration.getRedisPrefix() + ":" + LIMIT_TYPE.getType() + ":" + frequency.getName() + ":" + frequency.getTime();
        if (userId == null) {
            key += ":noUser:" + ip;
        } else {
            key += ":" + userId;
        }
        return key;
    }


    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams) {
        FrequencyVo frequency = strategyParams.getFrequency();
        int limit = frequency.getLimit();
        if (LIMIT_TYPE == frequency.getFreqLimitType() && limit > 0 && containLimit(limitItems, LIMIT_TYPE)) {
            int time = frequency.getTime();
            final String userId = strategyParams.getUserId();
            final String ip = strategyParams.getIp();
            String redisKey = getRedisKey(frequency, userId, ip);
            long v = redisTemplate.opsForValue().increment(redisKey, 1);
            if (v == 1) {
                redisTemplate.expire(redisKey, frequency.getTime(), TimeUnit.SECONDS);
            } else {
                //主要用于避免服务重启造成部份key变成永久key
                //低于60秒的也忽略，就让用户多等一下，否则就检查一下是否永久key
                if (v < limit) {
                    RedisTemplateUtil.expireTimeHashFrequencyCache(redisTemplate, redisKey, time, v);
                } else if (v > limit) {
                    String lang = strategyParams.getLang();
                    //再次过期处理，以免有变成永久的key
                    RedisTemplateUtil.expireTimeTTL(redisTemplate, redisKey, frequency.getTime());
                    if (whiteBlackCheckBiz.checkWhiteUserId(frequency, strategyParams.getCurTime(), userId)) {
                        logger.info("----doCheckLimit-userCount--whiteUserId-redisKey={} time={} count={} limit={} ip={}", redisKey, time, v, limit, ip);
                        //如果是功能白名单直接结束策略，不用往下执行
                        return;
                    }
                    logger.warn("----doCheckLimit-userCount--redisKey={} lang={} time={} count={} limit={} ip={}", redisKey, lang, time, v, limit, ip);
                    strategyParams.getChainOper().setFail(true);
                    FrequencyUtils.addFreqLog(strategyParams, limit, v, LIMIT_TYPE);
                    FrequencyUtils.failExceptionMsg(getLimitType(), frequency, lang);
                }
            }
        }
        limitChain.doCheckLimit(limitChain, strategyParams);
    }

}
