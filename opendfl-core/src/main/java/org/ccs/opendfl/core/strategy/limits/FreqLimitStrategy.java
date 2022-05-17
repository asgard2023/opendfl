package org.ccs.opendfl.core.strategy.limits;

import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;

public interface FreqLimitStrategy {
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams);

    public default boolean containLimit(String limitItems, FreqLimitType limitType) {
        return limitItems.contains(limitType.getCode() + ",");
    }

    public String getLimitType();
}
