package org.ccs.opendfl.core.exception;

public class DataNotExistException extends BaseException {
    private static final ResultCode resultCode = ResultCode.DATA_NOT_EXIST;
    private static final long serialVersionUID = 1L;

    public DataNotExistException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public DataNotExistException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
