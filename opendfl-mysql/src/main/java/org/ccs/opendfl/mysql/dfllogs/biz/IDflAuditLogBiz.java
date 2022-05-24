package org.ccs.opendfl.mysql.dfllogs.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dfllogs.po.DflAuditLogPo;

/**
 * @Version V1.0
 * @Title: IDflAuditLogBiz
 * @Description: 后台管理审计日志 业务接口
 * @Author: Created by chenjh
 * @Date: 2022-5-6 23:20:31
 */
public interface IDflAuditLogBiz extends IBaseService<DflAuditLogPo> {

    /**
     * 后台管理审计日志 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:20:31
     */
    Integer saveDflAuditLog(DflAuditLogPo entity);

    /**
     * 后台管理审计日志 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:20:31
     */
    Integer updateDflAuditLog(DflAuditLogPo entity);

    /**
     * 后台管理审计日志 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:20:31
     */
    Integer deleteDflAuditLog(Long id, Integer operUser, String remark);
}