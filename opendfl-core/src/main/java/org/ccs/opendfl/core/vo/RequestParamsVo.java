package org.ccs.opendfl.core.vo;

import org.ccs.opendfl.core.limitlock.RequestLock;

import javax.servlet.http.HttpServletRequest;

public class RequestParamsVo implements Cloneable {
    protected HttpServletRequest request;
    protected RequestLock requestLock;

    protected Long curTime;
    protected String dataId;
    protected String ip;

    public RequestParamsVo() {

    }

    public void load(HttpServletRequest request, String ip) {
        this.request = request;
        this.ip = ip;
    }

    public void load(RequestLock requestLock, Long curTime, String userId) {
        this.requestLock = requestLock;
        this.dataId = userId;
        this.curTime = curTime;
    }


    /**
     * 用于提高创建对象的性能
     *
     */
    public static final RequestParamsVo instance = new RequestParamsVo();

    public static RequestParamsVo newInstance() {
        return new RequestParamsVo();
    }

    @Override
    public RequestParamsVo clone() {
        RequestParamsVo obj = null;
        try {
            obj = (RequestParamsVo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
