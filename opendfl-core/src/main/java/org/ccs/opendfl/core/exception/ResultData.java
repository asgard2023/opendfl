package org.ccs.opendfl.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Return result type
 * @author chenjh
 */
public class ResultData {
    private String resultCode;
    private String errorMsg;
    private Object data;
    private String errorType = "sys";

    private ResultData(String resultCode, String errorMsg, Object data, String errorType) {
        this.resultCode = resultCode;
        this.errorMsg = errorMsg;
        this.data = data;
        this.errorType = errorType;
    }

    static Logger logger = LoggerFactory.getLogger(ResultData.class);


    public static ResultData error(String errorMsg) {
        return error(new UnknownException(errorMsg));
    }

    public static ResultData error(String resultCode, String errorMsg) {
        return new ResultData(resultCode, errorMsg, null, "sys");
    }

    public static ResultData error(BaseException myException) {
        String title= myException.getTitle();
        if(title==null){
            title="biz";
        }
        return new ResultData(myException.getResultCode(), myException.getMessage(), null, title);
    }

    public static ResultData success() {
        return new ResultData(ResultCode.DEFAULT_SUCCESS.getCode(), ResultCode.DEFAULT_SUCCESS.getMsg(), null, "no");
    }

    public static ResultData success(Object data) {
        return new ResultData(ResultCode.DEFAULT_SUCCESS.getCode(), ResultCode.DEFAULT_SUCCESS.getMsg(), data, "no");
    }


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Boolean getSuccess() {
        return ResultCode.DEFAULT_SUCCESS.getCode().equals(resultCode);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
