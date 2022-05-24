package org.ccs.opendfl.mysql.dflbasedata.po;

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
 * @Title: DflTypeItemPo
 * @Description: 实体
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:31:25
 */
@Data
@Table(name = "dfl_type_item")
@XmlRootElement(name = "dflTypeItem")
@JsonInclude(Include.ALWAYS)
public class DflTypeItemPo implements Serializable {
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
     * 类型id
     */
    @Column(name = "type_id")
    private Integer typeId;
    @Transient
    private DflTypePo type;

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

    private String color;

    /**
     * 值
     */
    @Column(name = "value")
    private String value;

    /**
     * 值2
     */
    @Column(name = "value2")
    private String value2;

    /**
     * 排序号
     */
    @Column(name = "order_count")
    private Integer orderCount;

    /**
     * 是否删除
     */
    @Column(name = "if_del")
    private Integer ifDel;

    /**
     * 状态
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

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