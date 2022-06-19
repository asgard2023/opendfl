package org.ccs.opendfl.core.strategy.limits.impl;

import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
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
 * 用户访问频率检查
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

    public static String getRedisKey(FrequencyVo frequency, String userId, String attrValue, String ip) {
        final String redisKey = frequencyConfiguration.getRedisPrefix();
        String key = redisKey + ":" + frequency.getName() + ":" + frequency.getTime();
        if (frequency.isResource() && frequency.getIpUserCount() == 0) {
            key += ":" + attrValue;
        }
        if (userId == null) {
            key += ":noUser:" + ip;
        } else {
            key += ":" + userId;
        }
        return key;
    }

    private boolean isFreqTypeLimit(FrequencyVo frequency) {
        //资源限制类型
        String freqDataType = frequencyConfiguration.getLimit().getResourceLimitType();
        //检查限制类型是否支持用户资源限制
        boolean isFreqData = freqDataType.contains("data");
        if (isFreqData && frequency.getUserIpCount() == 0) {
            return true;
        }
        //检查限制类型是否支持用户资源限制
        boolean isFreqIp = freqDataType.contains("ip");
        if (isFreqIp && frequency.getUserIpCount() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            FrequencyVo frequency = strategyParams.getFrequency();
            int time = frequency.getTime();
            final String userId = strategyParams.getUserId();
            final String attrValue = strategyParams.getAttrValue();
            final String ip = strategyParams.getIp();
            if (filterResource(frequency, userId, attrValue)) {
                limitChain.doCheckLimit(limitChain, strategyParams);
                return;
            }
            String redisKey = getRedisKey(frequency, userId, attrValue, ip);
            long v = redisTemplate.opsForValue().increment(redisKey, 1);
            int limit = frequency.getLimit();
            if (v == 1) {
                redisTemplate.expire(redisKey, frequency.getTime(), TimeUnit.SECONDS);
            } else {
                if (v > limit) {
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

    private boolean filterResource(FrequencyVo frequency, String userId, String dataId) {
        //是否资源限制
        if (frequency.isResource()) {
            //资源IP限制不处理
            if (frequency.getIpUserCount()>0) {
                return true;
            }
            //如果限制类型不支持，不处理这个限制
            if (!isFreqTypeLimit(frequency)) {
                return true;
            }
            //资源限制时userId与dataId不同，如果一样表示配置有问题，忽略这个检查，以免引起线上问题
            if (StringUtils.equals(userId, dataId)) {
                logger.warn("---doCheckLimit--name={} resource={} userId=dataId", frequency.getName(), frequency.isResource());
                return true;
            }
        }
        return false;
    }


}
