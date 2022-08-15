package org.ccs.opendfl.mysql.dflcore.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflcore.po.DflLocksPo;

/**
 * IDflLocksBiz
 * 分布式锁配置表 业务接口
 *
 * @author chenjh
 * @date 2022-5-18 21:44:08
 */
public interface IDflLocksBiz extends IBaseService<DflLocksPo> {

    /**
     * 分布式锁配置表 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:44:08
     */
    Integer saveDflLocks(DflLocksPo entity);

    /**
     * 分布式锁配置表 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:44:08
     */
    Integer updateDflLocks(DflLocksPo entity);

    /**
     * 分布式锁配置表 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:44:08
     */
    Integer deleteDflLocks(Integer id, Integer operUser, String remark);

    void getLockByCode_evict(String code);

    DflLocksPo getLockByCode(String code);
}