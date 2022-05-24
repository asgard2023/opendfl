package org.ccs.opendfl.mysql.dflsystem.biz;

import org.ccs.opendfl.mysql.dflsystem.po.DflUserRolePo;
import org.ccs.opendfl.mysql.vo.UserVo;

import java.util.List;

public interface IDflUserLoginBiz {
    /**
     * 用户登入
     *
     * @param username 账号
     * @param pwd      密码
     * @return 用户UserVo
     */
    UserVo loginUser(String username, String pwd);

    /**
     * 按token获取用户
     *
     * @param token token
     * @return 用户UserVo
     */
    UserVo getUserByToken(String token);

    /**
     * 保存token
     *
     * @param token       token
     * @param loginedUser 登入的用户对象
     */
    void saveUserByToken(String token, UserVo loginedUser);

    /**
     * 直接按token获取用户权限
     *
     * @param token token
     * @return 角色权限RolePermitVo
     */
    List<DflUserRolePo> getUserPermitByToken(String token);

    /**
     * 获取角色权限
     *
     * @param userId 角色编码
     * @return 角色权限RolePermitVo
     */
    public List<DflUserRolePo> getRolePermit(Integer userId) ;
}
