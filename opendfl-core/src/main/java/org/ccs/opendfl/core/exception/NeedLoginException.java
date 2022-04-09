package org.ccs.opendfl.core.exception;

/**
 * 登入异常
 * @author chenjh
 */
public class NeedLoginException extends BaseException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public NeedLoginException(String errorMsg) {
		super(ResultCode.NEED_LOGIN, errorMsg);
	}

	public NeedLoginException() {
		super(ResultCode.NEED_LOGIN, ResultCode.NEED_LOGIN_MSG);
	}
}
