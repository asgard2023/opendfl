package org.ccs.opendfl.core.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 用一个filter取一下post的json报文，存到request.attribute中，以便于在频率限制拦截器获取
 * 并把包装request继续往下传
 * @author chenjh
 */
public class HttpServletRequestReplacedFilter implements Filter {

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest request2 = (HttpServletRequest) request;
            //忽略GET请求
            if (!request2.getMethod().equals("GET")) {
                requestWrapper = new MyHttpServletRequestWrapper(request2);
            }
        }

        if (requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

}