package org.ccs.opendfl.mysql.dflcore.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflcore.po.DflBlackWhiteItemPo;
import org.ccs.opendfl.mysql.dflcore.vo.DflBlackWhiteVo;

import java.util.List;

/**
 * IDflBlackWhiteItemBiz
 * 黑名单 业务接口
 *
 * @author chenjh
 * @date 2022-5-18 21:45:02
 */
public interface IDflBlackWhiteItemBiz extends IBaseService<DflBlackWhiteItemPo> {

    /**
     * 黑名单 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:45:02
     */
    Integer saveDflBlackWhiteItem(DflBlackWhiteItemPo entity);

    /**
     * 黑名单 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:45:02
     */
    Integer updateDflBlackWhiteItem(DflBlackWhiteItemPo entity);

    /**
     * 黑名单 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:45:02
     */
    Integer deleteDflBlackWhiteItem(Integer id, Integer operUser, String remark);

    public void findBlackWhiteList_evict(String type, Integer limitType);

    public List<DflBlackWhiteVo> findBlackWhiteList(String type, Integer limitType);

    public void findBlackWhiteMaxUpdateTime_evict(String type, Integer limitType);

    public Long findBlackWhiteMaxUpdateTime(String type, Integer limitType);
}