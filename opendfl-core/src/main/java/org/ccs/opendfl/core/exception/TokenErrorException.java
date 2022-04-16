package org.ccs.opendfl.core.exception;


public class TokenErrorException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_TOKEN_FAILED;
    private static final long serialVersionUID = 1L;

    public TokenErrorException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public TokenErrorException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
