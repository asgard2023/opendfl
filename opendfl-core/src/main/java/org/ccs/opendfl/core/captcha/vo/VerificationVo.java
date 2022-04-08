package org.ccs.opendfl.core.captcha.vo;
import lombok.Data;

import java.io.Serializable;

@Data
public class VerificationVo implements Serializable{

	private static final long serialVersionUID = 5766305073489135335L;
	/** 唯一标志*/
	private String sessionId;
	/** 用户ID*/
	private String userId;
	/** 第一次刷新时间*/
	private Long firstTime;
	/** 刷新次数 */
	private int refreshTimes;
	/** 验证码*/
	private String verificationCode;
	/** 失效标志 0过期，1有效，2操作过于频繁*/
	private String isValid;
	
	
}