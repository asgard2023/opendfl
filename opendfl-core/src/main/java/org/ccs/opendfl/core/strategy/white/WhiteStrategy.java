package org.ccs.opendfl.core.strategy.white;

import org.ccs.opendfl.core.constants.FreqLimitType;

/**
 * 白名单策略
 *
 * @author chenjh
 */
public interface WhiteStrategy {
    boolean doCheckLimit(String limitItems, WhiteChain whiteChain);

    default boolean containLimit(String limitItems, FreqLimitType limitType) {
        return limitItems.contains(limitType.getCode() + ",");
    }

    String getLimitType();
}
