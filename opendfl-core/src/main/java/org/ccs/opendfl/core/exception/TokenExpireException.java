package org.ccs.opendfl.core.exception;


public class TokenExpireException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_TOKEN_EXPIRE;
    private static final long serialVersionUID = 1L;

    public TokenExpireException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public TokenExpireException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
