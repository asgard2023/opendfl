package org.ccs.opendfl.core.exception;


public class TokenErrorException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TokenErrorException(String errorMsg) {
        super(ResultCode.USER_TOKEN_FAILED, errorMsg);
    }

    public TokenErrorException() {
        super(ResultCode.USER_TOKEN_FAILED, ResultCode.USER_TOKEN_FAILED_MSG);
    }
}
