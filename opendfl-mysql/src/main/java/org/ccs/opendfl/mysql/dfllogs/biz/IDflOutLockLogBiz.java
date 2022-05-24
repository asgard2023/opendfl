package org.ccs.opendfl.mysql.dfllogs.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLockLogPo;

/**
 * @Version V1.0
 * @Title: IDflOutLockLogBiz
 * @Description: 分布式锁超限日志 业务接口
 * @Author: Created by chenjh
 * @Date: 2022-5-6 23:22:04
 */
public interface IDflOutLockLogBiz extends IBaseService<DflOutLockLogPo> {

    /**
     * 分布式锁超限日志 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:22:04
     */
    Integer saveDflOutLockLog(DflOutLockLogPo entity);

    /**
     * 分布式锁超限日志 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:22:04
     */
    Integer updateDflOutLockLog(DflOutLockLogPo entity);

    /**
     * 分布式锁超限日志 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:22:04
     */
    Integer deleteDflOutLockLog(Long id, Integer operUser, String remark);
}