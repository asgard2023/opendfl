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
 * dfl_user
*/
@Data
@Table(name = "dfl_user")
@JsonInclude(Include.ALWAYS)
public class UserPo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 
     */
    @Id
    private Integer id;
    
	/**
     * 
     */
    private String username;
    
	/**
     * 
     */
    private String pwd;
    
	/**
     * 
     */
    private String role;
    
	/**
     * 
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
	/**
     * 
     */
    @Column(name = "create_user")
    private String createUser;
    
	/**
     * 
     */
    @Column(name = "update_time")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
	/**
     * 
     */
    @Column(name = "update_user")
    private String updateUser;
    
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
    @Column(name = "remark")
    private String remark;

}