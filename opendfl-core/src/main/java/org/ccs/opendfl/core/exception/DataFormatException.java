package org.ccs.opendfl.core.exception;


public class DataFormatException extends BaseException {

    private static final ResultCode resultCode = ResultCode.DATA_FORMAT_ERROR;
    private static final long serialVersionUID = 1L;

    public DataFormatException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public DataFormatException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
