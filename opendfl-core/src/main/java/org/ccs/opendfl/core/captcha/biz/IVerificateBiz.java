package org.ccs.opendfl.core.captcha.biz;

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
     * @param clientId sessionId
     * @return 验证码
     */
    String setVerficationCode(String clientId);


    /**
     * 验查验证码
     * @param clientId sessionId
     * @param verificationCode 验证码
     */
    void checkVerificationCode(String clientId, String verificationCode);

    /**
     * 图片验证码
     *
     * @param type 类型
     * @param clientId sessionId
     * @param response httpResponse
     * @throws IOException 异常
     * @throws FontFormatException 异常
     */
    public void captchaOutputStream(CaptchaType type, String clientId, HttpServletResponse response) throws IOException, FontFormatException;
}
