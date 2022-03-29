package org.ccs.opendfl.core.exception;

public class ResultCode {
    private ResultCode() {

    }

    /**
     * 100-000<= 通用 <100-999
     */
    public static final String DEFAULT_SUCCESS_CODE = "100000";
    public static final String DEFAULT_SUCCESS_CODE_MSG = "成功";

    public static final String DEFAULT_FAILED_CODE = "100001";
    public static final String DEFAULT_FAILED_CODE_MSG = "失败";

    public static final String PARAMS_NULL = "100002";
    public static final String PARAMS_NULL_MSG = "请输入参数";

    public static final String ERROR_UNKNOWN = "100003";
    public static final String ERROR_UNKNOWN_MSG = "服务器内部错误";


    public static final String DATA_NOT_EXIST = "100006";
    public static final String DATA_NOT_EXIST_MSG = "查询数据不存在";

    public static final String DATA_FORMAT_ERROR = "100007";
    public static final String DATA_FORMAT_ERROR_MSG = "数据格式不对";

    public static final String DATA_EXIST = "100008";
    public static final String DATA_EXIST_MSG = "数据已存在";

    public static final String PARAMS_ERROR = "100009";
    public static final String PARAMS_ERROR_MSG = "参数错误";

    public static final String USER_FREQUENCY_ERROR = "100010";
    public static final String USER_FREQUENCY_ERROR_MSG = "访问频率限制";

    public static final String DECRYPT_ERROR = "101022";
    public static final String DECRYPT_ERROR_MSG = "参数解密失败";

    public static final String PERMISSION_DENIED = "101023";
    public static final String PERMISSION_DENIED_MSG = "没有权限";
}

