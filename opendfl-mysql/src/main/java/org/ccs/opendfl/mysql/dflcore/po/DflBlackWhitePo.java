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
 * @Title: DflBlackWhitePo
 * @Description: 黑白名单 实体
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:44:47
 */
@Data
@Table(name = "dfl_black_white")
@XmlRootElement(name = "dflBlackWhite")
@JsonInclude(Include.ALWAYS)
public class DflBlackWhitePo implements Serializable {
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
     * black/white
     */
    @Column(name = "type")
    private String type;

    /**
     * 1 user, 2 ip 3 deviceId 4 dataId
     */
    @Column(name = "limit_type")
    private Integer limitType;

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
    @Column(name = "remark")
    private String remark;

    /**
     *
     */
    @Column(name = "if_del")
    private Integer ifDel;

    /**
     *
     */
    @Column(name = "status")
    private Integer status;

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