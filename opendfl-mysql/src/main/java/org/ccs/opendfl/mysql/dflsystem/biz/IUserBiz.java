package org.ccs.opendfl.mysql.dflsystem.biz;

import com.github.pagehelper.PageInfo;
import org.ccs.opendfl.mysql.dflsystem.po.UserPo;

import java.util.Map;

/**
 * 用户管理
 *
 * @author chenjh
 */
public interface IUserBiz {
    public PageInfo<UserPo> findPageBy(UserPo entity, PageInfo<UserPo> pageInfo, Map<String, Object> otherParams);

    public UserPo findById(Integer id);

    /**
     * 保存
     *
     * @param entity
     * @return Integer
     * @author Chenjh
     * @date 2022-3-30 21:27:43
     */
    Integer saveUser(UserPo entity);

    /**
     * 更新
     *
     * @param entity
     * @return Integer
     * @author Chenjh
     * @date 2022-3-30 21:27:43
     */
    Integer updateUser(UserPo entity);

    /**
     * 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author Chenjh
     * @date 2022-3-30 21:27:43
     */
    Integer deleteUser(Integer id, String operUser, String remark);
}