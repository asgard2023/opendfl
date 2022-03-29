package org.ccs.opendfl.core.exception;

public class ParamErrorException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ParamErrorException(String errorMsg) {
        super(ResultCode.PARAMS_NULL, errorMsg);
    }

    public ParamErrorException() {
        super(ResultCode.PARAMS_ERROR, ResultCode.PARAMS_ERROR_MSG);
    }
}
