package org.ccs.opendfl.core.strategy.white;

import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;

/**
 * 白名单策略
 *
 * @author chenjh
 */
public interface WhiteStrategy {
    boolean doCheckLimit(String limitItems, WhiteChain whiteChain, RequestStrategyParamsVo strategyParams);

    default boolean containLimit(String limitItems, FreqLimitType limitType) {
        return limitItems.contains(limitType.getCode() + ",");
    }

    String getLimitType();
}
