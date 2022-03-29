package org.ccs.opendfl.core.exception;


public class PermissionDeniedException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PermissionDeniedException(String errorMsg) {
        super(ResultCode.PERMISSION_DENIED, errorMsg);
    }

    public PermissionDeniedException() {
        super(ResultCode.PERMISSION_DENIED, ResultCode.PERMISSION_DENIED_MSG);
    }
}
