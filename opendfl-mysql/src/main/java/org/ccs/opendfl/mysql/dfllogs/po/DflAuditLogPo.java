package org.ccs.opendfl.mysql.dfllogs.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * DflAuditLogPo
 * 后台管理审计日志 实体
 *
 * @author chenjh
 * @date 2022-5-6 23:20:31
 */
@Data
@Table(name = "dfl_audit_log")
@XmlRootElement(name = "dflAuditLog")
@JsonInclude(Include.ALWAYS)
public class DflAuditLogPo implements Serializable {
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
     *
     */
    @Column(name = "uri")
    private String uri;
    @Transient
    private DflRequestScansPo uriPo;

    /**
     *
     */
    @Column(name = "uri_id")
    private Integer uriId;

    /**
     *
     */
    @Column(name = "user_id")
    private Integer userId;
    @Transient
    private UserVo user;

    /**
     *
     */
    @Column(name = "role_id")
    private Integer roleId;

    /**
     * 用户IP
     */
    @Column(name = "ip")
    private String ip;

    /**
     * 系统类型
     */
    @Column(name = "sys_type")
    private Character sysType;

    /**
     * 操作类型
     */
    @Column(name = "oper_type")
    private String operType;

    /**
     *
     */
    @Column(name = "attr_data")
    private String attrData;

    private String remark;

    /**
     *
     */
    @Column(name = "times")
    private String times;

    /**
     *
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}