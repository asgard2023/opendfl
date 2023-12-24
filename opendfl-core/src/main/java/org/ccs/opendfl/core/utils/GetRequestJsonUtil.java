package org.ccs.opendfl.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GetRequestJsonUtil {
    private GetRequestJsonUtil() {

    }

    public static JSONObject getRequestJsonObject(HttpServletRequest request) throws IOException {
        String json = getRequestJsonString(request);
        return JSON.parseObject(json);
    }

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request httpRequest
     * @return 返回请求json
     * @throws IOException
     */

    public static String getRequestJsonString(HttpServletRequest request)
            throws IOException {
        String submitMehtod = request.getMethod();
        // GET
        if ("GET".equals(submitMehtod)) {
            return new String(request.getQueryString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replaceAll("%22", "\"");
            // POST
        } else {
            return getRequestPostStr(request);
        }
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     *
     * @param request httpRequest
     * @return 返回post数据
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }

        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;

    }

    /**
     * 描述:获取 post 请求内容
     *
     * @param request httpRequest
     * @return 返回post内容
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request)
            throws IOException {

        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = StandardCharsets.UTF_8.name();
        }
        return new String(buffer, charEncoding);
    }
}
