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
 * Origin白名单检查
 * 这个白名单与其他白名单功能不一样
 * 如果未配置origins表示功能未启用
 * 在白名单的origin能访问，不在的不能访问（header的origin为空也不行）
 *
 * @author chenjh
 */
@Service(value = "whiteOriginStrategy")
public class WhiteOriginStrategy implements WhiteStrategy {
    private static final Logger logger = LoggerFactory.getLogger(WhiteOriginStrategy.class);
    public static final WhiteBlackCheckType LIMIT_TYPE = WhiteBlackCheckType.ORIGIN;
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
            if (!frequencyConfiguration.getWhiteBlackCheckBiz().isIncludeWhiteId(userId, LIMIT_TYPE)) {
                //方便测试，日志前1000条
                if (FrequencyUtils.isInitLog(getLimitType())) {
                    logger.info("----doCheckLimit-whiteUser={} uri={}", userId, strategyParams.getRequestUri());
                }
                strategyParams.getChainOper().setWhiteStrategy(this);
                strategyParams.getChainOper().setFail(true);
                return false;
            }
        }
        return limitChain.doCheckLimit(limitChain, strategyParams);

    }
}
