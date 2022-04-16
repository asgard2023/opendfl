package org.ccs.opendfl.core.exception;

public enum ResultCode {
    DEFAULT_SUCCESS("100000", "成功"),
    DEFAULT_FAILED("100001", "失败"),
    ERROR_UNKNOWN("100002", "服务器内部错误"),
    DATA_NOT_EXIST("100003", "查询数据不存在"),
    DATA_EXIST("100004", "数据已存在"),

    PARAMS_NULL("100010", "请输入参数"),
    PARAMS_ERROR("100011", "参数错误"),
    DATA_FORMAT_ERROR("100012", "数据格式错误"),
    DECRYPT_ERROR("100013", "参数解密失败"),
    USER_TOKEN_FAILED("100020", "Token验证失败"),
    USER_TOKEN_EXPIRE("101021", "登录状态已经过期"),
    NEED_LOGIN("100022", "需要登入"),
    PERMISSION_DENIED("100023", "没有权限"),

    USER_FREQUENCY_ERROR("100030", "访问频率限制"),

    USER_IMAGE_CAPTCHA_ERROR("101001", "图片验证码错误"),
    USER_IMAGE_CAPTCHA_EXPIRE("101002", "图片验证码过期，请重新获取"),
    USER_CAPTCHA_RETRY_FREQUENT("101003", "验证码重试频繁"),
    USER_SMS_CAPTCHA_EXPIRE("101004", "短信验证码过期"),
    USER_CAPTCHA_EXPIRE("101005", "图片验证码过期"),
    USER_CAPTCHA_ERROR("101006", "验证码错误");

    private String code;
    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return msg;
    }
}

