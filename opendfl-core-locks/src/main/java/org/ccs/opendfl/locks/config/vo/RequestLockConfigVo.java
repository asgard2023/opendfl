package org.ccs.opendfl.locks.config.vo;

import lombok.Data;

@Data
public class RequestLockConfigVo {
    private String name;
    private Integer time;
    private String attrName;
    private String errMsg;
}
