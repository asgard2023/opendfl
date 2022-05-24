package org.ccs.opendfl.mysql.dfllogs.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * @Version V1.0
 * @Title: DflOutLockLogPo
 * @Description: 分布式锁超限日志 实体
 * @Author: Created by chenjh
 * @Date: 2022-5-6 23:22:04
 */
@Data
@Table(name = "dfl_out_lock_log")
@XmlRootElement(name = "dflOutLockLog")
@JsonInclude(Include.ALWAYS)
public class DflOutLockLogPo implements Serializable {
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
    @Column(name = "uid")
    private Long uid;

    @Column(name = "user_id")
    private String userId;

    @Transient
    private DflLogUserPo user;

    /**
     *
     */
    @Column(name = "attr_value")
    private String attrValue;

    /**
     * 锁类型:redis,etcdKv,etcdLock,zk
     */
    @Column(name = "lock_type")
    private String lockType;

    /**
     *
     */
    @Column(name = "time_second")
    private Integer timeSecond;

    /**
     *
     */
    @Column(name = "sys_type")
    private Character sysType;

    /**
     *
     */
    @Column(name = "ip")
    private String ip;

    /**
     *
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     *
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String remark;

}