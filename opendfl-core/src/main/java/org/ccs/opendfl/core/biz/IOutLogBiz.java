package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IOutLogBiz {
    final Logger logger = LoggerFactory.getLogger(IOutLogBiz.class);

    public default void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, Long v, FreqLimitType type) {
        logger.debug("----addFreqLog--type={} uri={} limit={} userId={} ip={} v={}", type, strategyParams.getRequestUri(), limit, strategyParams.getIp(), v);
    }

    public default void addLockLog(RequestLockVo requestLockVo, String userId, String ip, String deviceId, String sysType, String attrName, String dataId, String remark) {
        logger.debug("----addLockLog--uri={} time={} userId={} ip={} attrName={} dataId={}", requestLockVo.getRequestUri(), requestLockVo.getTime(), userId, ip, attrName, dataId);
    }
}
