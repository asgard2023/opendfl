package org.ccs.opendfl.core.exception;

/**
 * 登入异常
 * @author chenjh
 */
public class NeedLoginException extends BaseException {
	private static final ResultCode resultCode = ResultCode.NEED_LOGIN;
	private static final long serialVersionUID = 1L;

	public NeedLoginException(String errorMsg) {
		super(resultCode.getCode(), errorMsg);
	}

	public NeedLoginException() {
		super(resultCode.getCode(), resultCode.getMsg());
	}
}
