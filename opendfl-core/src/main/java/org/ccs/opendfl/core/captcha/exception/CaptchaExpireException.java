package org.ccs.opendfl.core.captcha.exception;


import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;

public class CaptchaExpireException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_CAPTCHA_EXPIRE;
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public CaptchaExpireException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
