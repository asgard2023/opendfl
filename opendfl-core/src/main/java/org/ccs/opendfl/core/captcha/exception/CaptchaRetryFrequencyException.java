package org.ccs.opendfl.core.captcha.exception;


import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;

public class CaptchaRetryFrequencyException extends BaseException {
    private static final ResultCode resultCode = ResultCode.USER_CAPTCHA_RETRY_FREQUENT;
    private static final long serialVersionUID = 1L;

    public CaptchaRetryFrequencyException(String errorMsg) {
        super(resultCode.getCode(), errorMsg);
    }

    public CaptchaRetryFrequencyException() {
        super(resultCode.getCode(), resultCode.getMsg());
    }
}
