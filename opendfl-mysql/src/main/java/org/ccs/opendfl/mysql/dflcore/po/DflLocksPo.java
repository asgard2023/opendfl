package org.ccs.opendfl.mysql.dflcore.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * @Version V1.0
 * @Title: DflLocksPo
 * @Description: 分布式锁配置表 实体
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:44:08
 */
@Data
@Table(name = "dfl_locks")
@XmlRootElement(name = "dflLocks")
@JsonInclude(Include.ALWAYS)
public class DflLocksPo implements Serializable {
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
     *
     */
    @Column(name = "uri")
    private String uri;

    /**
     *
     */
    @Column(name = "uri_id")
    private Integer uriId;

    /**
     *
     */
    @Column(name = "code")
    private String code;

    /**
     *
     */
    @Column(name = "name")
    private String name;

    /**
     *
     */
    @Column(name = "time")
    private Integer time;

    /**
     * redis,etcdKv,etcdLock,zk
     */
    @Column(name = "lock_type")
    private String lockType;

    /**
     *
     */
    @Column(name = "attr_name")
    private String attrName;

    /**
     *
     */
    @Column(name = "err_msg")
    private String errMsg;

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
     * 创建时间
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改时间
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

    private String remark;

}