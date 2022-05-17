package org.ccs.opendfl.core.strategy.white;


import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;

/**
 * 白名单责任链
 * 特点：满足任一名名单条件，直接通过
 *
 * @author chenjh
 */

public interface WhiteChain {
    public void sortStrategies(String limitItems);

    public boolean doCheckLimit(WhiteChain limitChain, RequestStrategyParamsVo strategyParams);
}
