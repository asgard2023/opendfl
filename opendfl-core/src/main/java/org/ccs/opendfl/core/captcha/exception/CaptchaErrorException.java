package org.ccs.opendfl.core.captcha.exception;


import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;

public class CaptchaErrorException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CaptchaErrorException(String errorMsg) {
        super(ResultCode.USER_CAPTCHA_ERROR, errorMsg);
    }

    public CaptchaErrorException() {
        super(ResultCode.USER_CAPTCHA_ERROR, ResultCode.USER_CAPTCHA_ERROR_MSG);
    }
}
