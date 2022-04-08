package org.ccs.opendfl.core.captcha.biz;

import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.captcha.constant.CaptchaType;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

/**
 * 验证码管理
 */
public interface IVerificateBiz {
    /**
     * 生成新验证码
     *
     * @param clientId String
     * @return
     * @throws BaseException
     */
    String setVerficationCode(String clientId);


    /**
     * 验查验证码
     * @param clientId
     * @param verificationCode
     */
    void checkVerificationCode(String clientId, String verificationCode);

    /**
     * 图片验证码
     *
     * @param type
     * @param clientId
     * @param response
     * @throws IOException
     * @throws FontFormatException
     */
    public void captchaOutputStream(CaptchaType type, String clientId, HttpServletResponse response) throws IOException, FontFormatException;
}
