package org.ccs.opendfl.core.exception;

public class DecryptException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DecryptException(String errorMsg) {
        super(ResultCode.DECRYPT_ERROR, errorMsg);
    }

    public DecryptException() {
        super(ResultCode.DECRYPT_ERROR, ResultCode.DECRYPT_ERROR_MSG);
    }
}
