package org.ccs.opendfl.mysql.dflsystem.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflsystem.po.DflUserRolePo;

import java.util.List;


/**
 * @Version V1.0
 * @Title: IDflUserRoleBiz
 * @Description: 用户角色 业务接口
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:26:31
 */
public interface IDflUserRoleBiz extends IBaseService<DflUserRolePo> {
    public List<DflUserRolePo> findUserRoles(Integer userId);

    /**
     * 用户角色 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:26:31
     */
    Integer saveDflUserRole(DflUserRolePo entity);

    /**
     * 用户角色 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:26:31
     */
    Integer updateDflUserRole(DflUserRolePo entity);

    /**
     * 用户角色 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:26:31
     */
    Integer deleteDflUserRole(Integer id, Integer operUser, String remark);
}