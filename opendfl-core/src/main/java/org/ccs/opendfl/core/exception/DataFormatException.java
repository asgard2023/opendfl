package org.ccs.opendfl.core.exception;


public class DataFormatException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DataFormatException(String errorMsg) {
        super(ResultCode.DATA_FORMAT_ERROR, errorMsg);
    }

    public DataFormatException() {
        super(ResultCode.DATA_FORMAT_ERROR, ResultCode.DATA_FORMAT_ERROR_MSG);
    }
}
