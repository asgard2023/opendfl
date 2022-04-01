package org.ccs.opendfl.core.strategy.white.impl;

import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.limitfrequency.FrequencyWhiteCodeUtils;
import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.strategy.white.WhiteStrategy;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * IP限限制检查
 */
@Service(value = "whiteIpStrategy")
public class WhiteIpStrategy implements WhiteStrategy {
    private static Logger logger = LoggerFactory.getLogger(WhiteIpStrategy.class);
    public static final FreqLimitType LIMIT_TYPE = FreqLimitType.WHITE_IP;


    @Override
    public String getLimitType() {
        return LIMIT_TYPE.getCode();
    }

    @Override
    public boolean doCheckLimit(String limitItems, WhiteChain limitChain) {
        if (containLimit(limitItems, LIMIT_TYPE)) {
            RequestStrategyParamsVo strategyParams = limitChain.getStrategyParams();
            String ip = strategyParams.getIp();
            if (StringUtils.isNumeric(ip)) {
                ip = RequestUtils.getNumConvertIp(Long.parseLong(ip));
            }
            WhiteBlackConfigVo whiteConfig = limitChain.getWhiteConfig();
            if (FrequencyWhiteCodeUtils.isWhiteId(ip, whiteConfig.getIps())) {
                //方便测试，日志前1000条
                if (FrequencyUtils.isInitLog(getLimitType())) {
                    logger.info("----doCheckLimit-whiteIp={} uri={}", ip, strategyParams.getRequestUri());
                }
                return true;
            }
        }
        return limitChain.doCheckLimit(limitChain);
    }
}
