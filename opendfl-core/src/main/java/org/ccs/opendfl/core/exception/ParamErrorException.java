package org.ccs.opendfl.core.exception;

public class ParamErrorException extends BaseException {
    private static final ResultCode resultCode = ResultCode.PARAMS_ERROR;
    private static final long serialVersionUID = 1L;

    public ParamErrorException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public ParamErrorException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
