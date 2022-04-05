package org.ccs.opendfl.core.filter;

import org.ccs.opendfl.core.utils.GetRequestJsonUtil;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *  取一下post的json报文，存到request.attribute中，以便于在频率限制拦截器获取
 *
 * @author chenjh
 */
public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] requestBody = null;

    public MyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        //缓存请求body
        try {
            requestBody = StreamUtils.copyToByteArray(request.getInputStream());
            String bodyStr = GetRequestJsonUtil.getRequestJsonString(request);
            request.setAttribute(RequestUtils.REQ_BODYS, bodyStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写 getInputStream()
     */

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (requestBody == null) {
            requestBody = new byte[0];
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };

    }

    /**
     * 重写 getReader()
     */

    @Override

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

}