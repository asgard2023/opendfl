package org.ccs.opendfl.core.vo;

import lombok.Data;

@Data
public class RequestLockVo {
    private String requestUri;
    private String name;
    private Integer time;
    private String attrName;
    private String errMsg;
    private Long createTime;
}
