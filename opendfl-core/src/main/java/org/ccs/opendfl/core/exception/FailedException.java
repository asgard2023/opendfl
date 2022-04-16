package org.ccs.opendfl.core.exception;

public class FailedException extends BaseException {
    private static final ResultCode resultCode = ResultCode.DEFAULT_FAILED;
    private static final long serialVersionUID = 1L;

    public FailedException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public FailedException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
