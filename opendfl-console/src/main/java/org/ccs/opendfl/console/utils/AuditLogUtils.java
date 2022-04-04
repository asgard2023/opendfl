package org.ccs.opendfl.console.utils;


import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.console.config.vo.UserVo;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 超限处理-审计日志，记录console的操作记录
     *
     * @param user 用户
     * @param operType 操作类型
     * @param attrData userId或account
     * @param times 频率限制间隔5,3600等
     */
    public static void addAuditLog(HttpServletRequest request, UserVo user, String operType, String attrData, Integer... times) {
        String uri = RequestUtils.getRequestUri(request);
        String timeStr = CommUtils.concat(",", times);
        log.info("----auditLog--uri={} operType={} user={} role={} time={} attrData={}"
                , uri, operType, user.getUsername(), user.getRole(), timeStr, attrData);
    }
}
