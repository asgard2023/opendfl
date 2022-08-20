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
 * DflSystemConfigPo
 * 系统参数配置(树形结构) 实体
 *
 * @author chenjh
 * @date 2022-5-3 20:27:48
 */
@Data
@Table(name = "dfl_system_config")
@JsonInclude(Include.ALWAYS)
public class DflSystemConfigPo implements Serializable {
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
     * 父id
     */
    @Column(name = "parent_id")
    private Integer parentId;

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
     * 参数值类型
     */
    @Column(name = "value_type")
    private Integer valueType;

    /**
     * 参数值
     */
    @Column(name = "value")
    private String value;

    @Column(name = "value_json")
    private String valueJson;
    /**
     * 初始默认值
     */
    @Column(name = "value_default")
    private String valueDefault;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 是否删除
     */
    @Column(name = "if_del")
    private Integer ifDel;

    /**
     * 状态：是否有效
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 系统编码
     */
    @Column(name = "system_code")
    private String systemCode;

    /**
     * 排序号
     */
    @Column(name = "order_count")
    private Integer orderCount;

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