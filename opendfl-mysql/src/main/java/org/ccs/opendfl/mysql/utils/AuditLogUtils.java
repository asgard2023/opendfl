package org.ccs.opendfl.mysql.utils;


import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflAuditLogBiz;
import org.ccs.opendfl.mysql.dfllogs.po.DflAuditLogPo;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 审计日志
 *
 * @author chenjh
 */
@Slf4j
@Component
public class AuditLogUtils {
    private AuditLogUtils() {

    }

    private static IDflAuditLogBiz dflAuditLogBiz;

    @Autowired
    public void setDflAuditLogBiz(IDflAuditLogBiz dflAuditLogBiz) {
        AuditLogUtils.dflAuditLogBiz = dflAuditLogBiz;
    }

    public static void addAuditLog(HttpServletRequest request, UserVo user, String operType, String attrData, Integer... times){
        addAuditLog(request, user, operType, attrData, null, times);
    }
    /**
     * 超限处理-审计日志，记录console的操作记录
     *
     * @param user     用户
     * @param operType 操作类型
     * @param attrData userId或account
     * @param times    频率限制间隔5,3600等
     */
    public static void addAuditLog(HttpServletRequest request, UserVo user, String operType, String attrData, String remark, Integer... times) {
        String uri = RequestUtils.getRequestUri(request);
        String timeStr = CommUtils.concat(",", times);
        String username=user!=null?user.getUsername():null;
        log.info("----auditLog--uri={} operType={} username={} time={} attrData={}"
                , uri, operType, username,  timeStr, attrData);
        DflAuditLogPo logPo = new DflAuditLogPo();
        logPo.setUri(request.getRequestURI());
        logPo.setSysType(RequestUtils.getSysTypeId(request));
        if(user!=null) {
            logPo.setUserId(user.getId());
        }
        logPo.setOperType(operType);
        logPo.setIp(RequestUtils.getIpAddress(request));
        logPo.setCreateTime(new Date());
        logPo.setAttrData(CommUtils.getStringLimit(attrData, 200));
        logPo.setTimes(CommUtils.concat(times, ","));
        logPo.setRemark(CommUtils.getStringLimit(remark, 200));
        dflAuditLogBiz.saveDflAuditLog(logPo);
    }
}
