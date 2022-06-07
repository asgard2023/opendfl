package org.ccs.opendfl.core.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.config.OpendflConfiguration;
import org.ccs.opendfl.core.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class ValidateUtils {
    private ValidateUtils() {

    }

    private static OpendflConfiguration opendflConfiguration;

    @Autowired
    public void setOpendflConfiguration(OpendflConfiguration opendfl) {
        ValidateUtils.opendflConfiguration = opendfl;
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
     * @param value
     * @param msg
     * @param values 多个值用英文“,”隔开
     * @throws BaseException
     * @Deprecated
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


    /**
     * 检查天数
     *
     * @param params
     * @param noDayLimits 如果这些参数有值，就不限制天数条件
     * @throws Exception
     */
    public static void checkTimeDateLimit(Map<String, Object> params, String noDayLimits) throws BaseException {
        int limitDay=opendflConfiguration.getBaseLimit().getSearchDateMaxDay();
        checkTimeDateLimit(params, noDayLimits, limitDay, 0);
    }

    /**
     * 检查天数
     *
     * @param params
     * @param noDayLimits 如果这些参数有值，就不限制天数条件
     * @param limitDay    天数限制
     * @throws Exception
     */
    public static void checkTimeDateLimit(Map<String, Object> params, String noDayLimits, int limitDay) throws BaseException {
        checkTimeDateLimit(params, noDayLimits, limitDay, 0);
    }

    private static boolean isParaValue(Map<String, Object> params, List<String> noLimitParams) {
        boolean isParaValue = false;
        for (String noLimit : noLimitParams) {
            Object paramValue = params.get(noLimit);
            if (paramValue != null && !"".equals(paramValue)) {
                isParaValue = true;
                break;
            }
        }
        return isParaValue;
    }

    /**
     * 检查天数
     *
     * @param params
     * @param noDayLimits 如果这些参数有值，就不限制天数条件
     * @param limitDay    天数限制
     * @throws Exception
     */
    public static void checkTimeDateLimit(Map<String, Object> params, String noDayLimits, int limitDay, int limitUserDay) throws BaseException {
        boolean isParamValue = false;
        if (StringUtils.isNotBlank(noDayLimits)) {
            List<String> noLimitParams = Arrays.asList(noDayLimits.split(","));
            if (isParaValue(params, noLimitParams)) {
                isParamValue = true;
                if (limitUserDay == 0) {
                    return;
                }
            }
        }
        String startTime = (String) params.get("startTime");
        if (startTime == null) {
            startTime = (String) params.get("startDate");
        }
        String endTime = (String) params.get("endTime");
        if (endTime == null) {
            endTime = (String) params.get("endDate");
        }
        if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
            throw new FailedException("开始时间，结束时间不能都为空");
        }


        Date startTimeDate = null;
        String timeValueDateFormat = "yyyyMMdd";
        if (StringUtils.isNotBlank(startTime)) {
            if (StringUtils.isNumeric(startTime) && startTime.length() == timeValueDateFormat.length()) {
                startTimeDate = DateUtil.parse(startTime, timeValueDateFormat);
            } else if (startTime.length() == DatePattern.NORM_DATE_PATTERN.length()) {
                startTimeDate = DateUtil.parseDate(startTime);
            } else {
                startTimeDate = DateUtil.parseDateTime(startTime);
            }
            if (startTimeDate == null) {
                throw new FailedException("开始时间无效");
            }
        }

        Date endTimeDate = null;
        if (StringUtils.isNotBlank(endTime)) {
            if (StringUtils.isNumeric(endTime) && endTime.length() == timeValueDateFormat.length()) {
                endTimeDate = DateUtil.parse(endTime, timeValueDateFormat);
            } else if (endTime.length() == DatePattern.NORM_DATE_PATTERN.length()) {
                endTimeDate = DateUtil.parseDate(endTime);
            } else {
                endTimeDate = DateUtil.parseDateTime(endTime);
            }
            if (endTimeDate == null) {
                throw new FailedException("结束时间无效");
            }
        }
        else{
            endTimeDate=new Date();
        }
        if (isParamValue && limitUserDay > 0) {
            checkDayLimit(limitUserDay, startTimeDate, endTimeDate);
            return;
        }

        checkDayLimit(limitDay, startTimeDate, endTimeDate);

        if (!"export".equals(params.get("opFuncType"))) {
            validPageCount(params);//检查要查的数据行数
        }
    }

    private static void checkDayLimit(int limitDay, Date startTimeDate, Date endTimeDate) {
        if (startTimeDate != null && endTimeDate != null) {
            long day = endTimeDate.getTime() / 3600 / 24000 - startTimeDate.getTime() / 3600 / 24000;
            if (day < 0) {
                throw new FailedException("结束时间小于开始时间");
            } else if (day > limitDay) {
                throw new FailedException("查询天数超出限制:" + limitDay);
            }
        }
    }

    private static Integer limitPageSize = 1000;
    private static Integer limitPageMax = 1000;
    private static Integer limitPageTotalRow = 10000;
    private static Long limitLoadTime = 0L;

    public static void loadLimit() {
        Long curTime = System.currentTimeMillis();
        if (limitPageSize == null || curTime - limitLoadTime > 60000) {
            limitLoadTime = curTime;
            limitPageSize = opendflConfiguration.getBaseLimit().getPageSizeMax();
            limitPageMax = opendflConfiguration.getBaseLimit().getPageNumMax();
            limitPageTotalRow = opendflConfiguration.getBaseLimit().getTotalRowMax();
        }
    }

    /**
     * 初始化查询分页对象
     *
     * @param params
     * @return
     * @throws BaseException
     */
    public static void validPageCount(Map<String, Object> params) throws BaseException {
        Object pageNumObj = params.get("pageNum");
        if (pageNumObj == null) {
            pageNumObj = params.get("page");
        }
        Object pageSizeObj = params.get("rows");
        if (pageSizeObj == null) {
            pageSizeObj = params.get("pageSize");
        }
        ValidateUtils.notNull(pageNumObj, "请输入页码参数page(或者pageNum)");
        ValidateUtils.notNull(pageSizeObj, "请输入页长参数pageSize");
        int pageNum = (Integer.parseInt(pageNumObj.toString()));
        int pageSize = (Integer.parseInt(pageSizeObj.toString()));
        loadLimit();
        if (pageSize > limitPageSize) {
            log.warn("----validPageCount--pageSize={} outLimit", pageSize);
            throw new FailedException("pageSize out limit");
        }
        if (pageNum > limitPageMax) {
            log.warn("----validPageCount--pageNum={} outLimit", pageNum);
            throw new FailedException("pageNum out limit");
        }
        int totalRow = pageNum * pageSize;
        if (totalRow > limitPageTotalRow) {
            log.warn("----validPageCount--totalRow={} outLimit", totalRow);
            throw new FailedException("pageNum out limit");
        }
    }

}
