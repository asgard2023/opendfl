package org.ccs.opendfl.mysql.dflsystem.po;

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
 * DflServicePo
 * 服务表 实体
 *
 * @author chenjh
 * @date 2022-5-4 11:19:03
 */
@Data
@Table(name = "dfl_service")
@XmlRootElement(name = "dflService")
@JsonInclude(Include.ALWAYS)
public class DflServicePo implements Serializable {
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
     * 服务类型
     */
    @Column(name = "service_type")
    private String serviceType;

    /**
     * 编码
     */
    @Column(name = "code")
    private String code;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 描述及简介
     */
    @Column(name = "descs")
    private String descs;

    /**
     * 作者及负责人
     */
    @Column(name = "author")
    private String author;

    /**
     * 服务接口url
     */
    @Column(name = "url")
    private String url;

    /**
     * 服务文档url
     */
    @Column(name = "wiki_url")
    private String wikiUrl;

    /**
     * 是否删除
     */
    @Column(name = "if_del")
    private Integer ifDel;

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

}