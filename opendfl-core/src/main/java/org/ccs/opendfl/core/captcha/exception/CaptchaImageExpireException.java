package org.ccs.opendfl.core.captcha.exception;


import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;

public class CaptchaImageExpireException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_IMAGE_CAPTCHA_EXPIRE;
    private static final long serialVersionUID = 1L;

    public CaptchaImageExpireException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public CaptchaImageExpireException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
