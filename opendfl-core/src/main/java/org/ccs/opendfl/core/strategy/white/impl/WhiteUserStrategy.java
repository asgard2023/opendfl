package org.ccs.opendfl.core.strategy.white.impl;

import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
import org.ccs.opendfl.core.constants.FreqLimitType;
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
 *
 * @author chenjh
 */
@Service(value = "whiteUserStrategy")
public class WhiteUserStrategy implements WhiteStrategy {
    private static final Logger logger = LoggerFactory.getLogger(WhiteUserStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.WHITE_USER;
    @Autowired
    private IWhiteBlackCheckBiz whiteBlackCheckBiz;

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, WhiteChain limitChain, final RequestStrategyParamsVo strategyParams) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            String userId = strategyParams.getUserId();
            if (whiteBlackCheckBiz.isIncludeWhiteId(userId, WhiteBlackCheckType.USER)) {
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
