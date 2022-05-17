package org.ccs.opendfl.core.strategy.black;

import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;

/**
 * 黑名单策略
 *
 * @author chenjh
 */
public interface BlackStrategy {
    boolean doCheckLimit(String limitItems, BlackChain blackChain, RequestStrategyParamsVo strategyParams);

    default boolean containLimit(String limitItems, FreqLimitType limitType) {
        return limitItems.contains(limitType.getCode() + ",");
    }

    String getLimitType();
}
