package org.ccs.opendfl.mysql.dfllogs.po;

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
 * @Title: DflRequestScansPo
 * @Description: 所有接口方法（通过扫码所有controller接口） 实体
 * @Author: Created by chenjh
 * @Date: 2022-5-10 22:12:24
 */
@Data
@Table(name = "dfl_request_scans")
@XmlRootElement(name = "dflRequestScans")
@JsonInclude(Include.ALWAYS)
public class DflRequestScansPo implements Serializable {
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
     * 接口uri
     */
    @Column(name = "uri")
    private String uri;

    /**
     * 方法名
     */
    @Column(name = "method_name")
    private String methodName;
    private String name;
    private String pkg;

    /**
     * 方法所有注解
     */
    @Column(name = "annotations")
    private String annotations;

    /**
     * 类名
     */
    @Column(name = "bean_name")
    private String beanName;

    /**
     * GET/POST/PUT等
     */
    @Column(name = "method")
    private String method;

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