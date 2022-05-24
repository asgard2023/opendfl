package org.ccs.opendfl.mysql.dflbasedata.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflbasedata.po.DflTypeItemPo;
import org.ccs.opendfl.mysql.vo.TypeItemVo;

import java.util.List;
import java.util.Map;


/**
 * @Version V1.0
 * @Title: IDflTypeItemBiz
 * @Description: 业务接口
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:31:24
 */
public interface IDflTypeItemBiz extends IBaseService<DflTypeItemPo> {
    public Map<String, List<TypeItemVo>> getItemsByTypes(String lang, String codes);

    public List<TypeItemVo> getItemsByType(String lang, String code);

    /**
     * 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:31:24
     */
    Integer saveDflTypeItem(DflTypeItemPo entity);

    /**
     * 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:31:24
     */
    Integer updateDflTypeItem(DflTypeItemPo entity);

    /**
     * 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:31:24
     */
    Integer deleteDflTypeItem(Integer id, Integer operUser, String remark);
}