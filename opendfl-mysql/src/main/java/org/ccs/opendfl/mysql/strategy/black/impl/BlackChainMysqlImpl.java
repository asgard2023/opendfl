package org.ccs.opendfl.mysql.strategy.black.impl;

import org.ccs.opendfl.core.strategy.black.BlackChain;
import org.ccs.opendfl.core.strategy.black.impl.BlackChainImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "blackChainMysql")
public class BlackChainMysqlImpl extends BlackChainImpl implements BlackChain {
    private static final Logger logger = LoggerFactory.getLogger(BlackChainMysqlImpl.class);

}
