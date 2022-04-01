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
    private String methodName;

    private String method;

    private Long createTime;
    private AtomicInteger counter;
    private Long maxRunTime;
    private Long maxRunTimeCreateTime;
    List<LimitUriConfigVo> limitRequests;
}
