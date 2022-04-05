package org.ccs.opendfl.core.utils;


import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.exception.ParamErrorException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class RequestUtils {
    private RequestUtils() {

    }


    public static final String DEFAULT_PARAM_NAME = "reqParams";

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
     * 阿里回调时使用   不解密
     *
     * @param request
     * @return
     * @throws BaseException
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, String> getParamsString(HttpServletRequest request) throws BaseException {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    /**
     * 获取请求参数    不加解密
     *
     * @param request
     * @return
     * @throws BaseException
     */
    public static Map<String, Object> getParamsObject(HttpServletRequest request) throws BaseException {
        Map<String, Object> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    public static final String REQ_BODYS = "reqBodys";

    /**
     * 从流获取请求参数
     *
     * @param request
     * @return
     */
    public static String getRequestParams(HttpServletRequest request) {
        StringBuilder sb = null;
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            if (request.getInputStream().isFinished()) {
                String reqBodys = (String) request.getAttribute(REQ_BODYS);
                if (reqBodys != null) {
                    request.removeAttribute(REQ_BODYS);
                }
                return reqBodys;
            }
            sb = new StringBuilder("");
            isr = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);
            br = new BufferedReader(isr);
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            String body = sb.toString();
            if (StringUtils.isNotBlank(body)) {
                request.setAttribute(REQ_BODYS, body);
            }
            return body;
        } catch (IOException e) {
            log.error("----getRequestParams-io-error={}", e.getMessage(), e);
            throw new ParamErrorException(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("----getRequestParams-br-error={}", e.getMessage());
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    log.error("----getRequestParams-isr-error={}", e.getMessage());
                }
            }
        }
    }

    public static String getLang(HttpServletRequest request) {
        String lang = request.getHeader(RequestParams.LANG);
        if (lang == null) {
            return LangCodes.ZH;//默认中文
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


    public static Integer getInt(Object obj) {
        if (obj == null || "".equals(obj)) {
            return null;
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return Integer.parseInt("" + obj);
    }

    public static Long getLong(Object obj) {
        if (obj == null || "".equals(obj)) {
            return null;
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
        String ip = request.getHeader("x-forwarded-for");
        if (isEmptyIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isEmptyIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isEmptyIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isEmptyIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isEmptyIp(ip)) {
            ip = request.getRemoteAddr();
        }
        if (isEmptyIp(ip)) {
            //最后都取不到ip
            return null;
        }
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "localhost";
        } else {
            ip = getIpFirst(ip);
            if (!isIpAddress(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        return ip;
    }

    public static String getIpFirst(String ip) {
        if (ip != null && ip.indexOf(',') > 0) {
            ip = ip.substring(0, ip.indexOf(','));
        }
        return ip;
    }

    public static String getIpAddressFirst(HttpServletRequest request) {
        String ip = getIpAddress(request);
        ip = getIpFirst(ip);
        return ip;
    }


    /**
     * 封装一层，主要抛出参数为空的异常
     *
     * @param params
     * @return
     * @throws BaseException
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
     * @param params
     * @param key
     * @return
     * @throws BaseException
     */
    public static double getDoubleFromMap(Map<String, Object> params, String key) throws BaseException {
        String value = getStringFromMap(params, key);
        try {
            return Double.parseDouble(value);// 交易值
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
        long mask[] = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};
        long num = 0;
        StringBuilder ipInfo = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0)
                ipInfo.insert(0, ".");
            ipInfo.insert(0, Long.toString(num, 10));
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
        if (StringUtils.equals("localhost", ipAddress)) {
            ipAddress = "127.0.0.1";
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
        if (length < 8 || length > 48) {
            log.warn("----checkIpLength--address={} length={} invalid", address, length);
            return true;
        }
        return false;
    }
}
