package org.ccs.opendfl.core.captcha.exception;


import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;

public class CaptchaImageErrorException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_IMAGE_CAPTCHA_ERROR;
    private static final long serialVersionUID = 1L;

    public CaptchaImageErrorException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public CaptchaImageErrorException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
