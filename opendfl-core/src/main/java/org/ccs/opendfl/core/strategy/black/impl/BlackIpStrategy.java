package org.ccs.opendfl.core.strategy.black.impl;

import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
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
 *
 * @author chenjh
 */
@Service(value = "blackIpStrategy")
public class BlackIpStrategy implements BlackStrategy {
    private static final Logger logger = LoggerFactory.getLogger(BlackIpStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.BLACK_IP;
    @Autowired
    private IWhiteBlackCheckBiz whiteBlackCheckBiz;

    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, BlackChain limitChain, final RequestStrategyParamsVo strategyParams) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            String userId = strategyParams.getUserId();
            String ip = strategyParams.getIp();

            if (whiteBlackCheckBiz.isIncludeBlackId(ip, WhiteBlackCheckType.IP)) {
                if (StringUtils.isNumeric(ip)) {
                    ip = RequestUtils.getNumConvertIp(Long.parseLong(ip));
                }
                logger.warn("----doCheckLimit-blackIp={} userId={} uri={}", ip, userId, strategyParams.getRequestUri());
                strategyParams.setBlackStrategy(this);
                return true;
            }
        }
        return limitChain.doCheckLimit(limitChain, strategyParams);
    }
}
