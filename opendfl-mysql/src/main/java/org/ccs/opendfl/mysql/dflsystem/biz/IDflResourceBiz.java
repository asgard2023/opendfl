package org.ccs.opendfl.mysql.dflsystem.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflsystem.po.DflResourcePo;

/**
 * IDflResourceBiz
 * 业务接口
 *
 * @author chenjh
 * @date 2022-5-4 10:55:38
 */
public interface IDflResourceBiz extends IBaseService<DflResourcePo> {

    /**
     * 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-4 10:55:38
     */
    Integer saveDflResource(DflResourcePo entity);

    /**
     * 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-4 10:55:38
     */
    Integer updateDflResource(DflResourcePo entity);

    /**
     * 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-4 10:55:38
     */
    Integer deleteDflResource(Integer id, Integer operUser, String remark);
}