package org.ccs.opendfl.core.exception;

public class FrequencyException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FrequencyException(String errorMsg) {
        super(ResultCode.USER_FREQUENCY_ERROR, errorMsg);
    }

    public FrequencyException() {
        super(ResultCode.USER_FREQUENCY_ERROR, ResultCode.USER_FREQUENCY_ERROR_MSG);
    }
}
