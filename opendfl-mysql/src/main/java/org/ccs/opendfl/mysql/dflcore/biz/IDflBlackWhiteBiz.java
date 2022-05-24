package org.ccs.opendfl.mysql.dflcore.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflcore.po.DflBlackWhitePo;

import java.util.List;

/**
 * @Version V1.0
 * @Title: IDflBlackWhiteBiz
 * @Description: 黑白名单 业务接口
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:44:47
 */
public interface IDflBlackWhiteBiz extends IBaseService<DflBlackWhitePo> {

    /**
     * 黑白名单 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:44:47
     */
    Integer saveDflBlackWhite(DflBlackWhitePo entity);

    /**
     * 黑白名单 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:44:47
     */
    Integer updateDflBlackWhite(DflBlackWhitePo entity);

    /**
     * 黑白名单 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:44:47
     */
    Integer deleteDflBlackWhite(Integer id, Integer operUser, String remark);

    List<DflBlackWhitePo> findBlackWhiteByType(String type, Integer limitType);
}