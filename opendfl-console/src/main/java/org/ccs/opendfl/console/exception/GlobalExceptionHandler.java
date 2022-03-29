package org.ccs.opendfl.console.exception;


import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.exception.UnknownException;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 捕获异常统一处理
 *
 * @version v1.0
 * @modified by chenjh
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, Object> getRequestMap(HttpServletRequest req) {
        Map<String, Object> reqMap = new TreeMap<>();
        if (req == null) {
            return reqMap;
        }

        //获得request 相关信息
        String method = req.getMethod();
        reqMap.put("method", method);

        String noLogStr = "token,";
        Set<String> keys = req.getParameterMap().keySet();
        for (String key : keys) {
            if (noLogStr.contains(key + ",")) {
                continue;
            }
            reqMap.put(key, req.getParameter(key));
        }
        reqMap.remove("password");
        reqMap.put("remoteAddr", RequestUtils.getIpAddress(req));
        String requestURI = req.getRequestURI();
        reqMap.put("requestURI", requestURI);
        return reqMap;
    }


    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handleException(HttpServletRequest request, Exception ex) {
        if (ex != null && (ex instanceof HttpRequestMethodNotSupportedException
                || ex instanceof HttpMediaTypeNotSupportedException
                || ex instanceof HttpMediaTypeNotAcceptableException)) {
            logger.error("---handleException method={} uri={} error={}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
            ResultData resultData = ResultData.error(new FailedException());
            resultData.setErrorMsg(ex.getMessage());
            return resultData;
        }


        String messageError = ex != null ? ex.getMessage() : null;
        logger.error("---handleException method={} error={} \n request={}", request.getMethod(), messageError, this.getRequestMap(request), ex);

        ResultData resultData = ResultData.error(new UnknownException());
        resultData.setErrorType("sys");
        return resultData;
    }

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultData handleBaseException(HttpServletRequest request, BaseException e) {
        String logType = getLogExceptionTypeBase();
        Map<String, Object> parameterMap = this.getRequestMap(request);
        if (("full".equals(logType)) && !(e instanceof BaseException)) {
            logger.warn("----handleBaseException method={} request={}\n error:{}", request.getMethod(), parameterMap, e.getMessage(), e);
        } else {
            logger.warn("----handleBaseException method={} request={}\n error:{}", request.getMethod(), parameterMap, e.getMessage());
        }
        return ResultData.error(e);
    }


    private static String logExceptionTypeBase = "simple";
    private static String getLogExceptionTypeBase() {
        return logExceptionTypeBase;
    }

}