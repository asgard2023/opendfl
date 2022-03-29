package org.ccs.opendfl.core.vo;

import lombok.Data;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

@Data
@Getter
public class RequestStrategyParamsVo{
    private final HttpServletRequest request;
    private String requestUri;
    private final String lang;
    private final String ip;
    private final String methodName;

    private FrequencyVo frequency;
    private Long curTime;
    private String userId;

    public RequestStrategyParamsVo(HttpServletRequest request, String lang, String ip, String methodName, String requestUri, Long curTime) {
        this.request = request;
        this.lang = lang;
        this.ip = ip;
        this.methodName = methodName;
        this.requestUri = requestUri;
        this.curTime = curTime;
    }


    public void load(FrequencyVo frequency, String userId){
        this.frequency = frequency;
        this.userId = userId;
    }

}
