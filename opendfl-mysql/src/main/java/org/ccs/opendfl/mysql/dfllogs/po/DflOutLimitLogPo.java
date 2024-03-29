package org.ccs.opendfl.mysql.dfllogs.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * DflOutLimitLogPo
 * 频率限制超限日志 实体
 *
 * @author chenjh
 * @date 2022-5-6 23:21:44
 */
@Data
@Table(name = "dfl_out_limit_log")
@JsonInclude(Include.ALWAYS)
public class DflOutLimitLogPo implements Serializable {
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

    @Column(name = "frequency_id")
    private Integer frequencyId;
    @Transient
    private DflFrequencyPo frequency;

    /**
     * 用户端用户id
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
    @Column(name = "lang")
    private String lang;

    /**
     * 限制次数
     */
    @Column(name = "limit_count")
    private Integer limitCount;

    /**
     * 间隔时间
     */
    @Column(name = "time_second")
    private Integer timeSecond;

    /**
     *
     */
    @Column(name = "attr_value")
    private String attrValue;

    /**
     * 超限类型frequency,white,black
     */
    @Column(name = "out_limit_type")
    private Integer outLimitType;

    /**
     * 子类型：frequency,frequency2，frequency3，frequency4，frequency5
     */
    @Column(name = "sub_type")
    private String subType;

    /**
     * 如果outType=white/black则WhiteBlackCheckType:black,white则user,ip,origin,device
     * 如果outType=frequency则FreqLimitType:limit,limitIp,ipUser,userIp
     */
    @Column(name = "limit_type")
    private String limitType;

    @Column(name = "if_resource")
    private Integer ifResource;

    /**
     * i ios,a android h h5
     */
    @Column(name = "sys_type")
    private Character sysType;

    /**
     * 用户IP地址
     */
    @Column(name = "ip")
    private String ip;

    @Column(name = "origin")
    private String origin;


    /**
     * 设备号
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     * 请求次数
     */
    @Column(name = "req_count")
    private Integer reqCount;

    /**
     * 异常信息
     */
    @Column(name = "err_msg")
    private String errMsg;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 0未登入，1用户Id，2用户账号
     */
    @Column(name = "user_type")
    private Integer userType;

    /**
     *
     */
    @Column(name = "run_time")
    private Integer runTime;

}