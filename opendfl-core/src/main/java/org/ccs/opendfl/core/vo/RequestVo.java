package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;

import java.util.List;

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
    List<LimitUriConfigVo> limitRequests;
}
