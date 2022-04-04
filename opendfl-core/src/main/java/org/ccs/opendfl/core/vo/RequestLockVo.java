package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 分布式锁对象信息
 *
 * @author chenjh
 */
@Data
public class RequestLockVo{
    private String requestUri;
    private String name;
    private int time;
    private String attrName;
    private String errMsg;
    private Long createTime;
    private boolean sysconfig;

    public RequestLockVo toCopy(){
        RequestLockVo obj = new RequestLockVo();
        BeanUtils.copyProperties(this, obj);
        return obj;
    }
}
