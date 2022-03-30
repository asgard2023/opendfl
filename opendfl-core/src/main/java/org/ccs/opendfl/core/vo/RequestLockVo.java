package org.ccs.opendfl.core.vo;

import lombok.Data;

@Data
public class RequestLockVo implements Cloneable {
    private String requestUri;
    private String name;
    private Integer time;
    private String attrName;
    private String errMsg;
    private Long createTime;

    @Override
    public RequestLockVo clone(){
        RequestLockVo obj = null;
        try{
            obj = (RequestLockVo)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
