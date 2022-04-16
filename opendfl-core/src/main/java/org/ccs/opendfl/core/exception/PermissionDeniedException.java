package org.ccs.opendfl.core.exception;


public class PermissionDeniedException extends BaseException {
    private static final ResultCode resultCode = ResultCode.PERMISSION_DENIED;
    private static final long serialVersionUID = 1L;

    public PermissionDeniedException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public PermissionDeniedException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }

}
