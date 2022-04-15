package org.ccs.opendfl.core.strategy.white.impl;

import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.strategy.white.WhiteStrategy;
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
@Service(value = "whiteIpStrategy")
public class WhiteIpStrategy implements WhiteStrategy {
    private static final Logger logger = LoggerFactory.getLogger(WhiteIpStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.WHITE_IP;
    @Autowired
    private IWhiteBlackCheckBiz whiteBlackCheckBiz;


    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, WhiteChain limitChain) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            RequestStrategyParamsVo strategyParams = limitChain.getStrategyParams();
            String ip = strategyParams.getIp();
            if (whiteBlackCheckBiz.isIncludeWhiteId(ip, WhiteBlackCheckType.IP)) {
                //方便测试，日志前1000条
                if (FrequencyUtils.isInitLog(getLimitType())) {
                    if (StringUtils.isNumeric(ip)) {
                        ip = RequestUtils.getNumConvertIp(Long.parseLong(ip));
                    }
                    logger.info("----doCheckLimit-whiteIp={} uri={}", ip, strategyParams.getRequestUri());
                }
                limitChain.setWhiteStrategy(this);
                return true;
            }
        }
        return limitChain.doCheckLimit(limitChain);
    }
}
