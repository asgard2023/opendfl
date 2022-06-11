package org.ccs.opendfl.core.utils;


import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.ReqSysType;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FailedException;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数处理
 *
 * @author chenjh
 */
@Slf4j
public class RequestUtils {
    private RequestUtils() {

    }

    public static String getParam(HttpServletRequest request, Map<String, Object> params, String paramName) {
        String paraValue = (String) params.get(paramName);
        if (paraValue == null) {
            paraValue = request.getParameter(paramName);
        }
        if (paraValue == null) {
            paraValue = request.getHeader(paramName);
        }
        return paraValue;
    }


    /**
     * 获取请求参数    不加解密
     *
     * @param request HttpServletRequest
     * @return Map<String, Object>
     */
    public static Map<String, Object> getParamsObject(HttpServletRequest request) throws BaseException {
        Map<String, String[]> requestParams = request.getParameterMap();
        Map<String, Object> params = new HashMap<>(requestParams.size());
        String deviceId = request.getHeader(RequestParams.DEVICE_ID);
        //参数优先，header允许被参数覆盖
        if (deviceId != null) {
            params.put(RequestParams.DEVICE_ID, deviceId);
        }

        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String[] values = entry.getValue();
            String valueStr = "";
            int length = values.length;
            for (int i = 0; i < length; i++) {
                valueStr = (i == length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(entry.getKey(), valueStr);
        }

        String postStr = (String) request.getAttribute(REQ_BODYS);
        if (postStr != null) {
            params.put(REQ_BODYS, postStr);
            request.removeAttribute(REQ_BODYS);
        }
        return params;
    }

    public static final String REQ_BODYS = "reqBodys";
    public static final String LOCALHOST_IP = "127.0.0.1";
    public static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    public static final String LOCALHOST_HOST = "localhost";

    public static String getSysType(HttpServletRequest request) {
        return request.getHeader(RequestParams.SYS_TYPE);
    }

    public static String getOrigin(HttpServletRequest request) {
        String origin = request.getHeader(RequestParams.ORIGIN);
        if (origin == null) {
            return null;
        }
        origin = CommUtils.getDomain(origin);
        return origin;
    }

    public static Character getSysTypeId(HttpServletRequest request) {
        String sysType = getSysType(request);
        if (sysType == null) {
            return null;
        }
        ReqSysType reqSysType = ReqSysType.parseCode(sysType);
        if (reqSysType != null) {
            return reqSysType.getType();
        }
        log.error("----getSysTypeId--sysType={} invalid", sysType);
        return null;
    }


    public static String getLang(HttpServletRequest request) {
        String lang = request.getHeader(RequestParams.LANG);
        if (lang == null) {
            //默认中文
            return LangCodes.ZH;
        }
        if (lang.indexOf('-') != -1) {
            //如en-CN
            return lang.split("-")[0];
        }
        if (!LangCodes.ZH.equals(lang) && !LangCodes.EN.equals(lang) && !LangCodes.JA.equals(lang)) {
            return LangCodes.EN;
        }
        return lang;
    }

    public static String getToken(HttpServletRequest request) {
        return request.getHeader(RequestParams.AUTHORIZATION);
    }


    public static Integer getInt(Object obj, Integer defaultValue) {
        if (obj == null || "".equals(obj)) {
            return defaultValue;
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return Integer.parseInt("" + obj);
    }

    public static Long getLong(Object obj, Long defaultValue) {
        if (obj == null || "".equals(obj)) {
            return defaultValue;
        } else if (obj instanceof Long) {
            return (Long) obj;
        }
        return Long.parseLong("" + obj);
    }

    private static boolean isEmptyIp(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

    public static String getRequestUri(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = ServletUtil.getClientIP(request);
        if (LOCALHOST_IP.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_HOST;
        }
        return ip;
    }


    /**
     * 封装一层，主要抛出参数为空的异常
     *
     * @param params Map<String, Object>
     * @return String
     */
    public static String getStringFromMap(Map<String, Object> params, String key) throws BaseException {
        if (params == null) {
            throw new FailedException("params is null");
        }
        Object obj = params.get(key);
        ValidateUtils.notNull(obj, "缺少参数：" + key);
        return obj.toString();
    }

    /**
     * 从Map里取Double数据
     *
     * @param params Map<String, Object>
     * @param key    String
     * @return double
     */
    public static double getDoubleFromMap(Map<String, Object> params, String key) throws BaseException {
        String value = getStringFromMap(params, key);
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            throw new FailedException("参数" + key + "必须为数值");
        }
    }

    /**
     * 将数字转成ip地址
     *
     * @param ipLong 数字
     * @return 转换后的ip地址
     */
    public static String getNumConvertIp(long ipLong) {
        long[] mask = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};
        long num = 0;
        StringBuilder ipInfo = new StringBuilder();
        int length = mask.length;
        for (int i = 0; i < length; i++) {
            num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0) {
                ipInfo.insert(0, ".");
            }
            ipInfo.insert(0, num);
        }
        return ipInfo.toString();
    }

    /**
     * 将ip 地址转换成数字
     *
     * @param ipAddress 传入的ip地址
     * @return 转换成数字类型的ip地址
     */
    public static long getIpConvertNum(String ipAddress) {
        if (StringUtils.equals(LOCALHOST_HOST, ipAddress)) {
            ipAddress = LOCALHOST_IP;
        }
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
    }

    public static String convertIpv4(String ipAddress) {
        if (isIpv6Address(ipAddress)) {
            return ipAddress;
        }
        return "" + getIpConvertNum(ipAddress);
    }

    /**
     * 将ip 地址转换成数字
     *
     * @param ipAddress 传入的ip地址
     * @return 转换成数字类型的ip地址
     */
    public static String getIpConvertNums(String ipAddress) {
        if (StringUtils.isBlank(ipAddress)) {
            return ipAddress;
        }
        //none表示不起作，忽略
        if (FrequencyConstant.NONE.equals(ipAddress)) {
            return ipAddress;
        }
        if (ipAddress.indexOf(',') < 0) {
            return convertIpv4(ipAddress) + ",";
        } else {
            String[] ips = ipAddress.split(",");
            StringBuilder sb = new StringBuilder();
            for (String ip : ips) {
                sb.append(convertIpv4(ip)).append(",");
            }
            return sb.toString();
        }
    }

    public static boolean isIpv6Address(String address) {
        try {
            if (checkIpLength(address)) {
                return false;
            }
            final InetAddress inetAddress = InetAddress.getByName(address);
            return inetAddress instanceof Inet6Address;
        } catch (UnknownHostException e) {
            log.warn("----isIpv6Address--address={}", address);
            return false;
        }
    }

    public static boolean isIpAddress(String address) {
        try {
            if (checkIpLength(address)) {
                return false;
            }
            final InetAddress inetAddress = InetAddress.getByName(address);
            return inetAddress instanceof Inet6Address || inetAddress instanceof InetAddress;
        } catch (UnknownHostException e) {
            log.warn("----isIpAddress--address={}", address);
            return false;
        }
    }

    private static boolean checkIpLength(String address) {
        int length = address.length();
        int minIpLength = 8;
        int maxIpLength = 48;
        if (length < minIpLength || length > maxIpLength) {
            log.warn("----checkIpLength--address={} length={} invalid", address, length);
            return true;
        }
        return false;
    }
}
