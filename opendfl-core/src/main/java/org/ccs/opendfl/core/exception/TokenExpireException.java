package org.ccs.opendfl.core.exception;


public class TokenExpireException extends BaseException {

    private static final long serialVersionUID = 1L;


    public TokenExpireException(String errorMsg) {
        super(ResultCode.USER_TOKEN_EXPIRE, errorMsg);
    }

    public TokenExpireException() {
        super(ResultCode.USER_TOKEN_EXPIRE, ResultCode.USER_TOKEN_EXPIRE_MSG);
    }
}
