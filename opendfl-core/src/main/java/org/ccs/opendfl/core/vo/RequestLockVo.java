package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.springframework.beans.BeanUtils;

/**
 * 分布式锁对象信息
 *
 * @author chenjh
 */
@Data
public class RequestLockVo{
    public static RequestLockVo toLockVo(RequestLock lock){
        RequestLockVo lockVo = new RequestLockVo();
        lockVo.setName(lock.name());
        lockVo.setTime(lock.time());
        lockVo.setAttrName(lock.attrName());
        lockVo.setErrMsg(lock.errMsg());
        return lockVo;
    }
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
