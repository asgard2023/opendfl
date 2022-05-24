package org.ccs.opendfl.mysql.strategy.limits.impl;

import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitChainImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "freqLimitChainMysql")
public class FreqLimitChainMysqlImpl extends FreqLimitChainImpl implements FreqLimitChain {
    private static final Logger logger = LoggerFactory.getLogger(FreqLimitChainMysqlImpl.class);

}
