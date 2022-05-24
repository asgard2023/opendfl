package org.ccs.opendfl.mysql.dflsystem.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflsystem.po.DflUserPo;
import org.ccs.opendfl.mysql.vo.UserVo;

import java.util.List;
import java.util.Map;

/**
 * @Version V1.0
 * @Title: IDflUserBiz
 * @Description: 业务接口
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:24:48
 */
public interface IDflUserBiz extends IBaseService<DflUserPo> {
    public enum loginType {
        USERNAME("username"),
        TELEPHONE("telephone"),
        EMAIL("emmail");
        private String code;
        loginType(String code){
            this.code = code;
        }
    }
    public Map<Integer, UserVo> getUserMapByIds(List<Integer> userIds);


    public DflUserPo getUserByUsername(String username);

    public DflUserPo findUserByTelephone(String telephone);

    public DflUserPo getUserByEmail(String email);

    public DflUserPo getUserByNickName(String nickName);

    public Integer getUserIdByNickName(String nickname);

    public UserVo loginUser(loginType type, String account, String loginPwd);


    /**
     * 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:24:48
     */
    Integer saveDflUser(DflUserPo entity);

    /**
     * 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:24:48
     */
    Integer updateDflUser(DflUserPo entity);

    /**
     * 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:24:48
     */
    Integer deleteDflUser(Integer id, Integer operUser, String remark);

    /**
     * 用户自已改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public int updatePassword(Integer userId, String oldPassword, String newPassword);

    /**
     * 管理员改密码
     * @param userId
     * @param newPassword
     * @param remark
     * @param operId
     * @return
     */
    public int changePasswordMgr(Integer userId, String newPassword, String remark, Integer operId);
}