package org.ccs.opendfl.core.strategy.limits;

import org.ccs.opendfl.core.constants.FreqLimitType;

public interface FreqLimitStrategy {
    public void doCheckLimit(String limitItems, FreqLimitChain limitChain);

    public default boolean containLimit(String limitItems, FreqLimitType limitType) {
        return limitItems.contains(limitType.getCode() + ",");
    }

    public String getLimitType();
}
