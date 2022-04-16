package org.ccs.opendfl.core.exception;

public class ParamNullException extends BaseException {
	private static final ResultCode resultCode = ResultCode.PARAMS_NULL;
	private static final long serialVersionUID = 1L;

	public ParamNullException(String errorMsg) {
		super(resultCode.getCode(), errorMsg);
	}

	public ParamNullException() {
		super(resultCode.getCode(), resultCode.getMsg());
	}
}
