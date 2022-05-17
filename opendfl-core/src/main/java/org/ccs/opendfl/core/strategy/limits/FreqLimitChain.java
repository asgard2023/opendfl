package org.ccs.opendfl.core.strategy.limits;


import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 频率限制链，基于注解@Frequency或者于uriConfig的限制处理
 * 特点：优先注解@Frequency方式，再uriConfig限制方式，达到条件后就限制访问
 *
 * @author chenjh
 */
public interface FreqLimitChain {
//    public void clearLimit();
    public void sortStrategies(String limitItems);
    public void doCheckLimit(FreqLimitChain limitChain, RequestStrategyParamsVo strategyParams);
}
