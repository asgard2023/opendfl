package org.ccs.opendfl.mysql.strategy.white.impl;

import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.strategy.white.impl.WhiteChainImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "whiteChainMysql")
public class WhiteChainMysqlImpl extends WhiteChainImpl implements WhiteChain {
    private static final Logger logger = LoggerFactory.getLogger(WhiteChainMysqlImpl.class);

}
