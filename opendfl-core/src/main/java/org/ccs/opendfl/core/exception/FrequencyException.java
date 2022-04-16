package org.ccs.opendfl.core.exception;

public class FrequencyException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_FREQUENCY_ERROR;
    private static final long serialVersionUID = 1L;

    public FrequencyException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public FrequencyException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
