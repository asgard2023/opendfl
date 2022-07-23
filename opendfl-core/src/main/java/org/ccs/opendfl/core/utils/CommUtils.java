package org.ccs.opendfl.core.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 常用工具类型
 *
 * @author chenjh
 */
public final class CommUtils {
    private CommUtils() {

    }

    /**
     * 以ch字符开头
     *
     * @param str 字符串
     * @param ch  要检查的字符
     * @return true/false
     */
    public static boolean startWithChar(String str, char ch) {
        if (str == null || str.length() < 1) {
            return false;
        }
        return str.charAt(0) == ch;
    }

    /**
     * 以ch字符结尾
     *
     * @param str 字符串
     * @param ch  要检查的字符
     * @return true/false
     */
    public static boolean endWithChar(String str, char ch) {
        if (str == null || str.length() < 1) {
            return false;
        }
        return str.charAt(str.length() - 1) == ch;
    }

    public static String appendUrl(String url, String path) {
        if (url == null) {
            return null;
        }
        if (path == null) {
            path = "";
        }
        //相当于startsWith
        if (CommUtils.endWithChar(url, '/') && CommUtils.startWithChar(path, '/')) {
            return url + path.substring(1);
        }
        //相当于startsWith
        else if (CommUtils.endWithChar(url, '/') || CommUtils.startWithChar(path, '/')) {
            return url + path;
        }

        return url + "/" + path;
    }

    public static Object nvl(Object... objects) {
        for (Object obj : objects) {
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    /**
     * 取数据前maxLength位
     *
     * @param str       字符
     * @param maxLength 最多取的字符个数
     * @return 截取后的字符
     */
    public static String getStringLimit(String str, int maxLength) {
        return str != null && str.length() > maxLength ? str.substring(0, maxLength) : str;
    }

    public static String getStringFirst(String str, String split) {
        if (str == null) {
            return null;
        }
        int idx = str.indexOf(split);
        if (idx > 0) {
            return str.substring(0, idx);
        }
        return str;
    }

    public static String concat(Collection list, String splitChar) {
        if (list == null) {
            return null;
        }
        splitChar = splitChar != null ? splitChar : ",";

        StringBuilder sb = new StringBuilder();
        for (Object obj : list) {
            sb.append(obj);
            sb.append(splitChar);
        }

        String str = sb.toString();
        if (str.endsWith(splitChar)) {
            str = str.substring(0, str.length() - splitChar.length());
        }
        return str;
    }

    public static String concat(String splitChar, Object... objs) {
        if (objs == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Object obj : objs) {
            if (obj == null) {
                continue;
            }
            sb.append(obj + splitChar);
        }
        String str = sb.toString();
        if (str.endsWith(splitChar)) {
            str = str.substring(0, str.length() - splitChar.length());
        }
        return str;
    }

    public static String concat(Object[] list, String splitChar) {
        if (list == null) {
            return null;
        }
        List<Object> list2 = Arrays.asList(list);
        return concat(list2, splitChar);
    }

    /**
     * 去除尾部逗号
     *
     * @param str 字符串
     * @return 新字符串
     */
    public static String removeEndComma(String str) {
        if (str.endsWith(",")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * 去除开头逗号
     *
     * @param str 字符串
     * @return 新字符串
     */
    public static String removeStartComma(String str) {
        if (str.startsWith(",")) {
            str = str.substring(1);
        }
        return str;
    }

    public static String appendComma(String str) {
        if (str.endsWith(",")) {
            return str;
        }
        return str + ",";
    }

    public static List newList(Object... objects) {
        return Arrays.asList(objects);
    }

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    public static String ignoreHttp(String url) {
        if (url == null) {
            return null;
        }
        url = url.trim();
        if (url.startsWith(HTTP)) {
            url = url.substring(HTTP.length());
        } else if (url.startsWith(HTTPS)) {
            url = url.substring(HTTPS.length());
        }
        return url;
    }

    /**
     * 取网址的域名或IP
     *
     * @param url 网址
     * @return 域名或ip
     */
    public static String getDomain(String url) {
        if (url == null) {
            return null;
        }
        url = ignoreHttp(url);
        //忽略端口号
        if (url.indexOf(':') > 0) {
            url = url.substring(0, url.indexOf(':'));
        } else {
            if (url.indexOf('/') > 0) {
                url = url.substring(0, url.indexOf('/'));
            }
        }
        return url;
    }
}
