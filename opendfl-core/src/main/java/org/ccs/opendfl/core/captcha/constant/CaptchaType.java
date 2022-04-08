package org.ccs.opendfl.core.captcha.constant;

import java.util.Random;

/**
 * 支持的验证码类型
 * @author chenjh
 */
public enum CaptchaType {
    /**
     * 纯数字
     */
    NUM(1, "num"),
    /**
     * 数字+字母
     */
    STRING(2, "string"),
    /**
     * 算式
     */
    ARITHMETIC(3, "arithmetic");
    private int type;
    private String code;

    CaptchaType(int type, String code) {
        this.type = type;
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public static CaptchaType parse(String code) {
        if (code == null) {
            return null;
        }
        CaptchaType[] types = CaptchaType.values();
        for (CaptchaType captchaType : types) {
            if (captchaType.code.equals(code)) {
                return captchaType;
            }
        }
        return null;
    }

    private static Random random = new Random();

    public static CaptchaType randomType(){
        CaptchaType[] types = CaptchaType.values();
        int v=random.nextInt(types.length);
        return types[v];
    }
}
