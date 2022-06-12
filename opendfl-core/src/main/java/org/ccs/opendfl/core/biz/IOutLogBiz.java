package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.constants.OutLimitType;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IOutLogBiz {
    final Logger logger = LoggerFactory.getLogger(IOutLogBiz.class);

    public default void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, Long v, OutLimitType outType, String subLimit, Integer ifResource, String typeCode, String attrValue) {
        logger.debug("----addFreqLog--outType={} subLimit={} typeCode={} uri={} limit={} ifResource={} userId={} ip={} v={}", outType, subLimit, typeCode, strategyParams.getRequestUri(), limit, ifResource, strategyParams.getIp(), v, attrValue);
    }

    public default void addLockLog(RequestLockVo requestLockVo, String userId, String ip, String deviceId, String sysType, String attrName, String attrValue, String remark) {
        logger.debug("----addLockLog--uri={} time={} userId={} ip={} attrName={} dataId={}", requestLockVo.getRequestUri(), requestLockVo.getTime(), userId, ip, attrName, attrValue);
    }
}
