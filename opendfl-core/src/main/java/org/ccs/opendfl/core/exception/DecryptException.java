package org.ccs.opendfl.core.exception;

public class DecryptException extends BaseException {

    private static final ResultCode resultCode = ResultCode.DECRYPT_ERROR;
    private static final long serialVersionUID = 1L;

    public DecryptException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public DecryptException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
