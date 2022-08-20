package org.ccs.opendfl.mysql.dflsystem.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * DflUserRolePo
 * 用户角色 实体
 *
 * @author chenjh
 * @date 2022-5-3 20:26:31
 */
@Data
@Table(name = "dfl_user_role")
@JsonInclude(Include.ALWAYS)
public class DflUserRolePo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    /**
     *
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;
    @Transient
    private UserVo user;

    /**
     * 角色ID
     */
    @Column(name = "role_id")
    private Integer roleId;

    @Transient
    private DflRolePo role;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 扩展参数配置json
     */
    @Column(name = "ext_config")
    private String extConfig;

    /**
     * 是否删除
     */
    @Column(name = "if_del")
    private Integer ifDel;

    private Integer status;

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
     * 创建人
     */
    @Column(name = "create_user")
    private Integer createUser;

    /**
     * 修改人
     */
    @Column(name = "modify_user")
    private Integer modifyUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getStartTime() {
        return this.startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEndTime() {
        return this.endTime;
    }
}