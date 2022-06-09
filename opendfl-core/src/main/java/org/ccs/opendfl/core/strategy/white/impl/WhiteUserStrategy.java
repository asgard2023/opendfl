package org.ccs.opendfl.core.strategy.white.impl;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.strategy.white.WhiteStrategy;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * IP限限制检查
 * 在白名单的用户不限制频率
 *
 * @author chenjh
 */
@Service(value = "whiteUserStrategy")
public class WhiteUserStrategy implements WhiteStrategy {
    private static final Logger logger = LoggerFactory.getLogger(WhiteUserStrategy.class);
    public static final WhiteBlackCheckType LIMIT_TYPE = WhiteBlackCheckType.USER;
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, WhiteChain limitChain, final RequestStrategyParamsVo strategyParams) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            String userId = strategyParams.getUserId();
            if (frequencyConfiguration.getWhiteBlackCheckBiz().isIncludeWhiteId(userId, LIMIT_TYPE)) {
                //方便测试，日志前1000条
                if (FrequencyUtils.isInitLog(getLimitType())) {
                    logger.info("----doCheckLimit-whiteUser={} uri={}", userId, strategyParams.getRequestUri());
                }
                strategyParams.getChainOper().setWhiteStrategy(this);
                return true;
            }
        }
        return limitChain.doCheckLimit(limitChain, strategyParams);

    }
}
