package org.ccs.opendfl.core.exception;

public class ParamNullException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParamNullException(String errorMsg) {
		super(ResultCode.PARAMS_NULL, errorMsg);
	}
	
	public ParamNullException() {
		super(ResultCode.PARAMS_NULL, ResultCode.PARAMS_NULL_MSG);
	}
}
