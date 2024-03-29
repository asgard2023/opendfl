package org.ccs.opendfl.mysql.dfllogs.biz.impl;

import org.ccs.opendfl.core.biz.IOutLogBiz;
import org.ccs.opendfl.core.constants.OutLimitType;
import org.ccs.opendfl.core.constants.ReqSysType;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.ccs.opendfl.mysql.config.MysqlConfiguration;
import org.ccs.opendfl.mysql.dflcore.biz.IDflFrequencyBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflLogUserBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflOutLimitLogBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflOutLockLogBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflRequestScansBiz;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLimitLogPo;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLockLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Date;

@EnableAsync
@Service(value = "outLogMysqlBiz")
public class OutLogMysqlBiz implements IOutLogBiz {
    @Autowired
    private IDflOutLimitLogBiz dflOutLimitLogBiz;
    @Autowired
    private IDflOutLockLogBiz dflOutLockLogBiz;
    @Autowired
    private IDflRequestScansBiz dflRequestScansBiz;
    @Autowired
    private IDflFrequencyBiz dflFrequencyBiz;
    @Autowired
    private IDflLogUserBiz dflLogUserBiz;
    @Autowired
    private MysqlConfiguration mysqlConfiguration;

    @Async
    @Override
    public void addFreqLog(RequestStrategyParamsVo strategyParams, Integer limit, Long v, OutLimitType outType, String subType, Integer ifResource, String typeCode, String attrValue) {
        String userId = strategyParams.getUserId();
        String uri = strategyParams.getRequestUri();
        String ip = strategyParams.getIp();
        logger.debug("----addFreqLog--type={} uri={} limit={} userId={} ip={} v={}", typeCode, uri, limit, userId, ip, v);
        DflOutLimitLogPo logPo = new DflOutLimitLogPo();
        logPo.setLimitCount(limit);
        logPo.setReqCount(v.intValue());

        FrequencyVo frequencyVo = strategyParams.getFrequency();
        if (frequencyVo != null) {
            logPo.setTimeSecond(frequencyVo.getTime());
            logPo.setFrequencyId(dflFrequencyBiz.getFrequencyIdByCode(frequencyVo.getName(), frequencyVo.getFreqLimitType().getType(), frequencyVo.getTime()));
        }
        logPo.setOutLimitType(outType.getType());
        logPo.setSubType(subType);
        logPo.setLimitType(typeCode);
        logPo.setAttrValue(attrValue);
        logPo.setIp(ip);
        logPo.setOrigin(strategyParams.getOrigin());
        logPo.setUri(uri);
        logPo.setSysType(ReqSysType.getSysType(strategyParams.getSysType()));
        logPo.setLang(CommUtils.getStringLimit(strategyParams.getLang(), 10));
        if (mysqlConfiguration.getUserIdToNum() == 1) {
            logPo.setUid(dflLogUserBiz.getUid(userId, strategyParams.getSysType(), ip));
        } else {
            logPo.setUserId(userId);
        }
        logPo.setUriId(dflRequestScansBiz.getUriId(logPo.getUri()));
        logPo.setIfResource(ifResource);

        //uriId不为空时，uri不用保存
        if (logPo.getUriId() != null) {
            logPo.setUri(null);
        }
        logPo.setAttrValue(strategyParams.getUserId());
        logPo.setDeviceId(strategyParams.getDeviceId());
        logPo.setCreateTime(new Date(strategyParams.getCurTime()));
        Long runTime = System.currentTimeMillis() - strategyParams.getCurTime();
        logPo.setRunTime(runTime.intValue());
        dflOutLimitLogBiz.saveDflOutLimitLog(logPo);
    }

    @Async
    @Override
    public void addLockLog(RequestLockVo requestLockVo, String userId, String ip, String deviceId, String sysType, String attrName, String dataId, String remark) {
        logger.debug("----addLockLog--uri={} time={} userId={} ip={} attrName={} dataId={}", requestLockVo.getRequestUri(), requestLockVo.getTime(), userId, ip, attrName, dataId);
        DflOutLockLogPo logPo = new DflOutLockLogPo();
        logPo.setLockType(requestLockVo.getLockType());
        logPo.setTimeSecond(requestLockVo.getTime());
        if (mysqlConfiguration.getUserIdToNum() == 1) {
            logPo.setUid(dflLogUserBiz.getUid(userId, sysType, ip));
        } else {
            logPo.setUserId(userId);
        }
        logPo.setAttrValue(CommUtils.getStringLimit(dataId, 250));
        logPo.setIp(ip);
        logPo.setSysType(ReqSysType.getSysType(sysType));
        logPo.setUri(CommUtils.getStringLimit(requestLockVo.getRequestUri(), 250));
        logPo.setUriId(dflRequestScansBiz.getUriId(logPo.getUri()));

        //uriId不为空时，uri不用保存
        if (logPo.getUriId() != null) {
            logPo.setUri(null);
        }
        logPo.setDeviceId(deviceId);
        logPo.setCreateTime(new Date(requestLockVo.getCreateTime()));
        logPo.setRemark(CommUtils.getStringLimit(remark, 250));
        dflOutLockLogBiz.saveDflOutLockLog(logPo);
    }
}
