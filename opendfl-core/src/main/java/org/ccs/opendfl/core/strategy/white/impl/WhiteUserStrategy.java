package org.ccs.opendfl.core.strategy.white.impl;

import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.limitcount.FrequencyUtils;
import org.ccs.opendfl.core.limitcount.FrequencyWhiteCodeUtils;
import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.strategy.white.WhiteStrategy;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * IP限限制检查
 */
@Service(value = "whiteUserStrategy")
public class WhiteUserStrategy implements WhiteStrategy {
    private static Logger logger = LoggerFactory.getLogger(WhiteUserStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.WHITE_USER;

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, WhiteChain limitChain) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            RequestStrategyParamsVo strategyParams = limitChain.getStrategyParams();
            String userId = strategyParams.getUserId();
            WhiteBlackConfigVo whiteConfig = limitChain.getWhiteConfig();
            if (FrequencyWhiteCodeUtils.isWhiteId(userId, whiteConfig.getUsers())) {
                //方便测试，日志前1000条
                if (FrequencyUtils.isInitLog(getLimitType())) {
                    logger.info("----doCheckLimit-whiteUser={} uri={}", userId, strategyParams.getRequestUri());
                }
                return true;
            }
        }
        return limitChain.doCheckLimit(limitChain);

    }
}
