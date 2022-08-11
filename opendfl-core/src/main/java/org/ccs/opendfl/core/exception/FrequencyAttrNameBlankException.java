package org.ccs.opendfl.core.exception;

public class FrequencyAttrNameBlankException extends BaseException {
	private static final ResultCode resultCode = ResultCode.USER_FREQUENCY_ATTR_BLANK_ERROR;
	private static final long serialVersionUID = 1L;

	public FrequencyAttrNameBlankException(String errorMsg) {
		super(resultCode.getCode(), errorMsg);
	}

	public FrequencyAttrNameBlankException() {
		super(resultCode.getCode(), resultCode.getMsg());
	}
}
