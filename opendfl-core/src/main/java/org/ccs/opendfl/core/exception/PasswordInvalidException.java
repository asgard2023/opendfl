package org.ccs.opendfl.core.exception;

public class PasswordInvalidException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_PASSWORD_INVALID;
    private static final long serialVersionUID = 1L;

    public PasswordInvalidException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public PasswordInvalidException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }

}
