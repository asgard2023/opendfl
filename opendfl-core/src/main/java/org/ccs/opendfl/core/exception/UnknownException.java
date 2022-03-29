package org.ccs.opendfl.core.exception;


public class UnknownException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UnknownException(String errorMsg) {
        super(ResultCode.ERROR_UNKNOWN, errorMsg);
    }

    public UnknownException() {
        super(ResultCode.ERROR_UNKNOWN, ResultCode.ERROR_UNKNOWN_MSG);
    }
}
