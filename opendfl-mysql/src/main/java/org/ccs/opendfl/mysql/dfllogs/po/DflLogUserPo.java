package org.ccs.opendfl.mysql.dfllogs.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * DflLogUserPo
 * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 实体
 *
 * @author chenjh
 * @date 2022-5-10 22:16:27
 */
@Data
@Table(name = "dfl_log_user")
@JsonInclude(Include.ALWAYS)
public class DflLogUserPo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * userId
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 用户类型
     */
    @Column(name = "user_type")
    private Integer userType;

    /**
     * nickname
     */
    @Column(name = "nickname")
    private String nickname;
    /**
     * 注册ip
     */
    @Column(name = "register_ip")
    private String registerIp;

    @Column(name = "sys_type")
    private Character sysType;

    /**
     * remark
     */
    @Column(name = "remark")
    private String remark;

    /**
     * if_del
     */
    @Column(name = "if_del")
    private Integer ifDel;

    /**
     * create_time
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}