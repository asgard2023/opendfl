package org.ccs.opendfl.core.strategy.black.impl;

import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.strategy.black.BlackChain;
import org.ccs.opendfl.core.strategy.black.BlackStrategy;
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
@Service(value = "blackDeviceIdtrategy")
public class BlackDeviceIdtrategy implements BlackStrategy {
    private static final Logger logger = LoggerFactory.getLogger(BlackDeviceIdtrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.BLACK_DEVICE_ID;
    @Autowired
    private IWhiteBlackCheckBiz whiteBlackCheckBiz;

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, BlackChain limitChain) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            RequestStrategyParamsVo strategyParams = limitChain.getStrategyParams();
            String deviceId = strategyParams.getDeviceId();
            if (whiteBlackCheckBiz.isIncludeBlackId(deviceId, WhiteBlackCheckType.DEVICE)) {
                logger.warn("-----doCheckLimit-blackDeviceId={} uri={}", deviceId, strategyParams.getRequestUri());
                limitChain.setBlackStrategy(this);
                return true;
            }
        }
        return limitChain.doCheckLimit(limitChain);
    }
}
