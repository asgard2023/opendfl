package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class RequestVo {
    private String requestUri;
    /**
     * Conttroller类名
     */
    private String beanName;
    /**
     * 包名
     */
    private String pkg;
    private String methodName;
    private String annotations;

    private String method;

    private Long createTime;
    /**
     * 请求记数器
     */
    private AtomicInteger counter;
    /**
     * 失败或被限计数器
     */
    private AtomicInteger limitCounter;
    /**
     * 间格时间内最大执行时间
     */
    private Long maxRunTime=0L;
    /**
     * 间格时间内最大执行时间的发生时间
     */
    private Long maxRunTimeCreateTime=0L;
    List<LimitUriConfigVo> limitRequests;
}
