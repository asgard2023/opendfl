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
 * DflRolePo
 * 角色表 实体
 *
 * @author chenjh
 * @date 2022-5-3 20:25:42
 */
@Data
@Table(name = "dfl_role")
@JsonInclude(Include.ALWAYS)
public class DflRolePo implements Serializable {
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

}