
package org.ccs.opendfl.core.exception;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String resultCode;
    private String title = "Error";


    private static final Map<String, String> errorCodeMap;

    static {
        errorCodeMap = new ConcurrentHashMap<>();
        errorCodeMap.put(ResultCode.PARAMS_NULL, ResultCode.PARAMS_NULL_MSG);
        errorCodeMap.put(ResultCode.PARAMS_ERROR, ResultCode.PARAMS_ERROR_MSG);
        errorCodeMap.put(ResultCode.DEFAULT_FAILED_CODE, ResultCode.DEFAULT_FAILED_CODE_MSG);
        errorCodeMap.put(ResultCode.DATA_NOT_EXIST, ResultCode.DATA_NOT_EXIST_MSG);
        errorCodeMap.put(ResultCode.DATA_EXIST, ResultCode.DATA_EXIST_MSG);
        errorCodeMap.put(ResultCode.USER_FREQUENCY_ERROR, ResultCode.USER_FREQUENCY_ERROR_MSG);
    }


    private static String getMsg(String resultCode, String errorMsg) {
        if (errorMsg != null) {
            return errorMsg;
        }
        return errorCodeMap.get(resultCode);
    }

    public BaseException(String resultCode, String errorMsg) {
        super(getMsg(resultCode, errorMsg));
        this.resultCode = resultCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
