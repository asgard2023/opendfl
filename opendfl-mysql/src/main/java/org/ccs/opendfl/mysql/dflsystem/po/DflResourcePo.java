package org.ccs.opendfl.mysql.dflsystem.po;

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
 * DflResourcePo
 * 实体
 *
 * @author chenjh
 * @date 2022-5-4 10:55:39
 */
@Data
@Table(name = "dfl_resource")
@JsonInclude(Include.ALWAYS)
public class DflResourcePo implements Serializable {
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
     * 所属服务ID
     */
    @Column(name = "service_id")
    private Integer serviceId;

    /**
     * 接口uri
     */
    @Column(name = "uri")
    private String uri;

    /**
     *
     */
    @Column(name = "uri_id")
    private Integer uriId;

    /**
     * 请求类型(GET/POST/PUT)
     */
    @Column(name = "method")
    private String method;

    /**
     * 资源类型，0接口,1功能
     */
    @Column(name = "res_type")
    private Integer resType;

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