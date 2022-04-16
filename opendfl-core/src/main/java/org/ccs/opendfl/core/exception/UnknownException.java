package org.ccs.opendfl.core.exception;


public class UnknownException extends BaseException {
    private static final ResultCode resultCode = ResultCode.ERROR_UNKNOWN;
    private static final long serialVersionUID = 1L;

    public UnknownException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public UnknownException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
