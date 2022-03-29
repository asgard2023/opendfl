package org.ccs.opendfl.core.strategy.black;

import org.ccs.opendfl.core.constants.FreqLimitType;

public interface BlackStrategy {
    public boolean doCheckLimit(String limitItems, BlackChain blackChain);

    public default boolean containLimit(String limitItems, FreqLimitType limitType) {
        return limitItems.contains(limitType.getCode() + ",");
    }

    public String getLimitType();
}
