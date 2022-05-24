package org.ccs.opendfl.mysql.dflsystem.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * @Version V1.0
 * @Title: DflUserPo
 * @Description: 实体
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:24:48
 */
@Data
@Table(name = "dfl_user")
@XmlRootElement(name = "dflUser")
@JsonInclude(Include.ALWAYS)
public class DflUserPo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static UserVo toUserVo(DflUserPo user) {
        UserVo vo = new UserVo();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setUsername(user.getUsername());
        return vo;
    }

    /**
     *
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 昵称
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 电话
     */
    @Column(name = "telephone")
    private String telephone;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 密码
     */
    @Column(name = "pwd")
    private String pwd;

    /**
     * 是否删除
     */
    @Column(name = "if_del")
    private Integer ifDel;

    /**
     * 状态:是否有效0无效，1有效
     */
    @Column(name = "status")
    private Integer status;

    /**
     *
     */
    @Column(name = "remark")
    private String remark;

    /**
     *
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     *
     */
    @Column(name = "modify_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    /**
     *
     */
    @Column(name = "create_user")
    private Integer createUser;

    /**
     *
     */
    @Column(name = "modify_user")
    private Integer modifyUser;
}