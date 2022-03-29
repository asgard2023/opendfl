package org.ccs.opendfl.core.utils;

import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.DataFormatException;
import org.ccs.opendfl.core.exception.ParamErrorException;
import org.ccs.opendfl.core.exception.ParamNullException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public class ValidateUtils {
    private ValidateUtils(){

    }

    /**
     * 验证参数不能为空
     *
     * @param value
     * @param msg
     * @throws BaseException
     */
    public static void notNull(Object value, String msg) throws BaseException {
        if (value == null || value.toString().trim().length() == 0) {
            throw new ParamNullException(msg);
        }
    }


    /**
     * 这个方法主要是为编码时期出错提示的，非业务错误提示
     *
     * @param value
     * @param fieldName
     * @throws BaseException
     */
    public static void notNullForCoding(Object value, String fieldName) throws BaseException {
        if (value == null || value.toString().trim().length() == 0) {
            throw new ParamNullException(fieldName + " can not be null");
        }
    }

    /**
     * 这个方法主要是为编码时期出错提示的，非业务错误提示
     *
     * @param list
     * @param fieldName
     * @throws BaseException
     */
    public static void notNullForCoding(Collection<?> list, String fieldName) throws BaseException {
        if (CollectionUtils.isEmpty(list)) {
            throw new ParamNullException(fieldName + " can not be null");
        }
    }

    /**
     * 参证参数必须合理
     *
     * @Deprecated
     * @param value
     * @param msg
     * @param values 多个值用英文“,”隔开
     * @throws BaseException
     */
    public static void mustIn(Object value, String msg, String values) throws BaseException {
        if (StringUtils.isEmpty(values)) {
            return;
        }
        values += ",";
        if (value == null || values.indexOf(value.toString() + ",") == -1) {
            throw new ParamErrorException(msg);
        }
    }

    /**
     * 参证参数必须合理
     *
     * @param value
     * @param fieldName
     * @param values    数组
     * @throws BaseException
     */
    public static void mustIn(Object value, String fieldName, String... values) throws BaseException {
        if (StringUtils.isBlank(fieldName)) {
            throw new ParamErrorException(fieldName + " can not be null");
        }
        if (values == null || values.length == 0) {
            return;
        }
        String valueStrs = null;
        for (String strValue : values) {
            if (value.equals(strValue)) {
                return;
            }
            if (valueStrs == null) {
                valueStrs = strValue;
            } else {
                valueStrs += "," + strValue;
            }
        }
        throw new ParamErrorException(fieldName + " must in(" + valueStrs + ")");
    }

    /**
     * 验证最大长度，为空也算验证通过
     *
     * @param value
     * @param maxLen
     * @param msg
     * @throws BaseException
     */
    public static void maxLen(Object value, int maxLen, String msg) throws BaseException {
        if (value != null && value.toString().length() > maxLen) {
            throw new DataFormatException(msg);
        }
    }


    /**
     * 验证最小长度，为空会失败
     *
     * @param value
     * @param minLen
     * @param msg
     * @throws BaseException
     */
    public static void minLen(Object value, int minLen, String msg) throws BaseException {
        if (value == null || value.toString().length() < minLen) {
            throw new DataFormatException(msg);
        }
    }


    /**
     * 验证最大值整数，为空也算验证通过
     *
     * @param value
     * @param maxVal
     * @param msg
     * @throws BaseException
     */
    public static void maxIntegerVal(Integer value, int maxVal, String msg) throws BaseException {
        if (value != null && value > maxVal) {
            throw new DataFormatException(msg);
        }
    }

    /**
     * 验证最小值整数，为空验证不通过
     *
     * @param value
     * @param minVal
     * @param msg
     * @throws BaseException
     */
    public static void minIntegerVal(Integer value, int minVal, String msg) throws BaseException {
        if (value == null || value.intValue() < minVal) {
            throw new DataFormatException(msg);
        }
    }

    /**
     * 验证最小值整数，为空也验证不通过
     *
     * @param value
     * @param minVal
     * @param msg
     * @throws BaseException
     */
    public static void minDoubleVal(Double value, int minVal, String msg) throws BaseException {
        if (value == null || value.doubleValue() < minVal) {
            throw new DataFormatException(msg);
        }
    }

    /**
     * 正则表达式验证，如果字符串为空直接验证失败
     *
     * @param value
     * @param regStr
     * @param msg
     * @throws BaseException
     */
    public static void regStr(String value, String regStr, String msg) throws BaseException {
        if (value == null || !value.matches(regStr)) {
            throw new DataFormatException(msg);
        }
    }


}
