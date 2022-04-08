package org.ccs.opendfl.core.captcha.biz.impl;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ParamNullException;
import org.ccs.opendfl.core.exception.ResultCode;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.captcha.biz.IVerificateBiz;
import org.ccs.opendfl.core.captcha.constant.CaptchaType;
import org.ccs.opendfl.core.captcha.utils.CaptchaCreator;
import org.ccs.opendfl.core.captcha.vo.VerificationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VerificateBiz implements IVerificateBiz {
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    public static String getCaptcha() {
        int randomCode = (int) (Math.random() * 9000 + 1000);
        String verifyCode = String.valueOf(randomCode);
        return verifyCode;
    }

    /**
     * 限制时间
     */
    private final static Integer BAN_TIME_SECONDS = 600;
    /**
     * 限制时间内请求次数
     */
    private final static Integer TRY_TIMES = 10;
    /**
     * 失效时间
     */
    public final static Long CACHE_EXPIRE = 60L;

    @Resource(name = "redisTemplateJson")
    private RedisTemplate redisTemplateJson;

    @Override
    public String setVerficationCode(String clientId) {
        String captchaCode = getCaptcha();
        VerificationVo verificationCache = this.setVerifictionCode(clientId, captchaCode);
        if (StringUtils.equals(verificationCache.getIsValid(), "2")) {
            //重试频繁
            throw new BaseException(ResultCode.USER_CAPTCHA_RETRY_FREQUENT, ResultCode.USER_CAPTCHA_RETRY_FREQUENT_MSG);
        }
        return captchaCode;
    }

    @Override
    public void captchaOutputStream(CaptchaType type, String clientId, HttpServletResponse response) throws IOException, FontFormatException {
        VerificationVo verificationCache = null;

        if (CaptchaType.NUM == type ||CaptchaType.STRING == type) {
            //数字验证码
            CaptchaCreator captcha = new CaptchaCreator(100, 30, 4, 10, type.getCode());
            verificationCache = this.setVerifictionCode(clientId, captcha.getCode());
            if (StringUtils.equals(verificationCache.getIsValid(), "2")) {
                //重试频繁
                throw new BaseException(ResultCode.USER_CAPTCHA_RETRY_FREQUENT, ResultCode.USER_CAPTCHA_RETRY_FREQUENT_MSG);
            }
            captcha.write(response.getOutputStream());
        } else if (CaptchaType.STRING == type) {
            //数字验证码
            CaptchaCreator captcha = new CaptchaCreator(100, 30, 4, 10, type.getCode());
            verificationCache = this.setVerifictionCode(clientId, captcha.getCode());
            if (StringUtils.equals(verificationCache.getIsValid(), "2")) {
                //重试频繁
                throw new BaseException(ResultCode.USER_CAPTCHA_RETRY_FREQUENT, ResultCode.USER_CAPTCHA_RETRY_FREQUENT_MSG);
            }
            captcha.write(response.getOutputStream());
        } else {
            //算式验证码
            ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 30);
            captcha.setLen(3);  // 几位数运算，默认是两位
            captcha.getArithmeticString();  // 获取运算的公式：3+2=?
            verificationCache = this.setVerifictionCode(clientId, captcha.text());
            if (StringUtils.equals(verificationCache.getIsValid(), "2")) {
                //重试频繁
                throw new BaseException(ResultCode.USER_CAPTCHA_RETRY_FREQUENT, ResultCode.USER_CAPTCHA_RETRY_FREQUENT_MSG);
            }
            captcha.setFont(Captcha.FONT_2);
            captcha.out(response.getOutputStream());
        }
    }


//    @Override
//    public VerificationVo getVerficationCode(String sessionId) {
//        return this.getUserVerifictionCodeCahce(sessionId);
//    }

    private String gerRedisKeyVerify(String sessionId) {
        return frequencyConfiguration.getRedisPrefix() + ":verifyCode:" + sessionId;
    }

    public VerificationVo getUserVerifictionCodeCahce(String sessionId) {
        String key = gerRedisKeyVerify(sessionId);
        VerificationVo verificationCache = (VerificationVo) redisTemplateJson.opsForValue().get(key);
        if (verificationCache != null) {
            Integer refreshTimes = verificationCache.getRefreshTimes();
            if (refreshTimes >= TRY_TIMES) {
                verificationCache.setIsValid("2");
                redisTemplateJson.opsForValue().set(key, verificationCache, CACHE_EXPIRE, TimeUnit.SECONDS);
            }
        }
        return verificationCache;
    }

    public VerificationVo setVerifictionCode(String clientId, String verificationCode) {
        String key = gerRedisKeyVerify(clientId);
        VerificationVo verificationCache = (VerificationVo) redisTemplateJson.opsForValue().get(key);
        if (verificationCache != null) {
            this.validateVerficationCode(verificationCache, verificationCode);
        } else {
            verificationCache = new VerificationVo();
            verificationCache.setSessionId(clientId);
            verificationCache.setFirstTime(System.currentTimeMillis());
            verificationCache.setIsValid("1");
            verificationCache.setRefreshTimes(1);
            verificationCache.setVerificationCode(verificationCode);
        }
        redisTemplateJson.opsForValue().set(key, verificationCache, CACHE_EXPIRE, TimeUnit.SECONDS);
        return verificationCache;
    }

    private void validateVerficationCode(VerificationVo verificationCache, String verificationCode) {
        long curTime = System.currentTimeMillis();
        int seconds = (int) ((curTime - verificationCache.getFirstTime()) / 1000);
        if (seconds >= BAN_TIME_SECONDS) {
            verificationCache.setVerificationCode(verificationCode);
            verificationCache.setIsValid("1");
            verificationCache.setFirstTime(curTime);
            verificationCache.setRefreshTimes(1);
        } else {
            Integer refreshTimes = verificationCache.getRefreshTimes();
            if (refreshTimes >= TRY_TIMES) {
                verificationCache.setIsValid("2");
            } else {
                verificationCache.setIsValid("1");
                verificationCache.setVerificationCode(verificationCode);
                verificationCache.setRefreshTimes(verificationCache.getRefreshTimes() + 1);
            }
        }
    }


    private void setVerficationCodeInvalid(String sessionId) {
        String key = gerRedisKeyVerify(sessionId);
        VerificationVo verificationCache = (VerificationVo) redisTemplateJson.opsForValue().get(key);
        if (verificationCache != null) {
            verificationCache.setIsValid("0");
            redisTemplateJson.opsForValue().set(key, verificationCache, CACHE_EXPIRE, TimeUnit.SECONDS);
        }
    }


    /**
     * 验证码校码
     *
     * @param clientId
     * @param verificationCode
     */
    @Override
    public void checkVerificationCode(String clientId, String verificationCode) {
        if (StringUtils.isEmpty(clientId)) {
            throw new ParamNullException("clientId is null");
        }

        if (StringUtils.isEmpty(verificationCode)) {
            throw new ParamNullException("请输入验证码");
        }
        VerificationVo verificationCache = getUserVerifictionCodeCahce(clientId);
        if (null == verificationCache || StringUtils.equals(verificationCache.getIsValid(), "0")) {
            //验证码过期
            throw new BaseException(ResultCode.USER_IMAGE_CAPTCHA_EXPIRE, ResultCode.USER_IMAGE_CAPTCHA_EXPIRE_MSG);
        } else if (StringUtils.equals(verificationCache.getIsValid(), "2")) {
            //重试频繁
            throw new BaseException(ResultCode.USER_CAPTCHA_RETRY_FREQUENT, ResultCode.USER_CAPTCHA_RETRY_FREQUENT_MSG);
        }
        String serverVerificationCode = verificationCache.getVerificationCode();
        if (!StringUtils.equals(verificationCode, serverVerificationCode)) {
            //验证码错误,
            throw new BaseException(ResultCode.USER_IMAGE_CAPTCHA_ERROR, ResultCode.USER_IMAGE_CAPTCHA_ERROR_MSG);
        }
        //试过后把用户所有验证码设置无效
        setVerficationCodeInvalid(clientId);
    }
}
