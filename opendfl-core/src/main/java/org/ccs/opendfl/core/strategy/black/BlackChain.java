package org.ccs.opendfl.core.strategy.black;


import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.springframework.stereotype.Service;

/**
 * 黑名单链
 * 特点：满足任一黑名单，直接拒绝
 *
 * @author chenjh
 */
@Service
public interface BlackChain {

    public void sortStrategies(String limitItems);

    public boolean doCheckLimit(BlackChain limitChain, RequestStrategyParamsVo strategyParams);
}
