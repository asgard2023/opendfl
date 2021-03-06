package org.ccs.opendfl.core.captcha.exception;


import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;

public class CaptchaErrorException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_CAPTCHA_ERROR;
    private static final long serialVersionUID = 1L;

    public CaptchaErrorException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public CaptchaErrorException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
