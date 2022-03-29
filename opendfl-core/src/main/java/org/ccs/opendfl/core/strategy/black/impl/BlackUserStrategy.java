package org.ccs.opendfl.core.strategy.black.impl;

import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.limitcount.FrequencyWhiteCodeUtils;
import org.ccs.opendfl.core.strategy.black.BlackChain;
import org.ccs.opendfl.core.strategy.black.BlackStrategy;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * IP限限制检查
 */
@Service(value = "blackUserStrategy")
public class BlackUserStrategy implements BlackStrategy {
    private static Logger logger = LoggerFactory.getLogger(BlackUserStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.BLACK_USER;


    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, BlackChain limitChain) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            RequestStrategyParamsVo strategyParams = limitChain.getStrategyParams();
            String userId = strategyParams.getUserId();
            if (userId != null) {
                WhiteBlackConfigVo whiteConfig = limitChain.getBlackConfig();
                if (FrequencyWhiteCodeUtils.isWhiteId(userId, whiteConfig.getUsers())) {
                    logger.warn("-----doCheckLimit-blackUser={} uri={}", userId, strategyParams.getRequestUri());
                    return true;
                }
            }
        }
        return limitChain.doCheckLimit(limitChain);
    }
}
