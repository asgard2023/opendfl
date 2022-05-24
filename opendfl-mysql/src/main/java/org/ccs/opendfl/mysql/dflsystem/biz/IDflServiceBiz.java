package org.ccs.opendfl.mysql.dflsystem.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflsystem.po.DflServicePo;

/**
 * @Version V1.0
 * @Title: IDflServiceBiz
 * @Description: 服务表 业务接口
 * @Author: Created by chenjh
 * @Date: 2022-5-4 11:19:02
 */
public interface IDflServiceBiz extends IBaseService<DflServicePo> {

    /**
     * 服务表 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-4 11:19:02
     */
    Integer saveDflService(DflServicePo entity);

    /**
     * 服务表 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-4 11:19:02
     */
    Integer updateDflService(DflServicePo entity);

    /**
     * 服务表 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-4 11:19:02
     */
    Integer deleteDflService(Integer id, Integer operUser, String remark);
}