package org.ccs.opendfl.core.exception;

public class FailedException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FailedException(String errorMsg) {
        super(ResultCode.DEFAULT_FAILED_CODE, errorMsg);
    }

    public FailedException() {
        super(ResultCode.DEFAULT_FAILED_CODE, ResultCode.DEFAULT_FAILED_CODE_MSG);
    }
}
