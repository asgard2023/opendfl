package org.ccs.opendfl.core.strategy.black.impl;

import org.ccs.opendfl.core.biz.IWhiteBlackListBiz;
import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.strategy.black.BlackChain;
import org.ccs.opendfl.core.strategy.black.BlackStrategy;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * IP限限制检查
 */
@Service(value = "blackIpStrategy")
public class BlackIpStrategy implements BlackStrategy {
    private static final Logger logger = LoggerFactory.getLogger(BlackIpStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.BLACK_IP;
    @Autowired
    private IWhiteBlackListBiz whiteBlackListBiz;


    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, BlackChain limitChain) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            RequestStrategyParamsVo strategyParams = limitChain.getStrategyParams();
            String userId = strategyParams.getUserId();
            String ip = strategyParams.getIp();
            if (StringUtils.isNumeric(ip)) {
                ip = RequestUtils.getNumConvertIp(Long.parseLong(ip));
            }
            WhiteBlackConfigVo whiteConfig = whiteBlackListBiz.getBlackConfig();
            if (whiteBlackListBiz.isIncludeId(ip, whiteConfig.getIps())) {
                logger.warn("----doCheckLimit-blackIp={} userId={} uri={}", ip, userId, strategyParams.getRequestUri());
                limitChain.setBlackStrategy(this);
                return true;
            }
        }
        return limitChain.doCheckLimit(limitChain);
    }
}
