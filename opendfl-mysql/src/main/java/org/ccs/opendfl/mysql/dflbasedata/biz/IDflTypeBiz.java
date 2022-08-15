package org.ccs.opendfl.mysql.dflbasedata.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflbasedata.po.DflTypePo;

import java.util.List;
import java.util.Map;


/**

 * IDflTypeBiz
 * 业务接口
 * @author chenjh
 * @date 2022-5-3 20:31:07
 */
public interface IDflTypeBiz extends IBaseService<DflTypePo> {
    public Map<Integer, DflTypePo> getTypeMapByIds(List<Integer> typeIds);

    public List<DflTypePo> getTypeMapByCodes(List<String> codes);

    /**
     * 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:31:07
     */
    Integer saveDflType(DflTypePo entity);

    /**
     * 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:31:07
     */
    Integer updateDflType(DflTypePo entity);

    /**
     * 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:31:07
     */
    Integer deleteDflType(Integer id, Integer operUser, String remark);
}