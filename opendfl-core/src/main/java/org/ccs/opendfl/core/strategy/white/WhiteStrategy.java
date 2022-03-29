package org.ccs.opendfl.core.strategy.white;

import org.ccs.opendfl.core.constants.FreqLimitType;

public interface WhiteStrategy {
    public boolean doCheckLimit(String limitItems, WhiteChain whiteChain);

    public default boolean containLimit(String limitItems, FreqLimitType limitType) {
        return limitItems.contains(limitType.getCode() + ",");
    }

    public String getLimitType();
}
