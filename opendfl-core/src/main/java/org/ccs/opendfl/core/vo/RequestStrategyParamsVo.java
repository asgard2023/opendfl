package org.ccs.opendfl.core.vo;

import lombok.Data;
import lombok.Getter;

/**
 * 请求参数，用于限制处理
 * @author chenjh
 *
 */
@Data
@Getter
public class RequestStrategyParamsVo{
    private String requestUri;
    private final String lang;
    private final String methodName;

    private FrequencyVo frequency;
    private long curTime;
    private final String ip;
    private final String sysType;
    /**
     * 可以从url参数或者header参数取值
     * 默认取deviceId
     */
    private final String deviceId;
    /**
     * 可以从url参数，post的json参数，inputStream中的参数中取值
     * 默认取userId也可以是接口注解指定的attrName参数
     */
    private String userId;

    public RequestStrategyParamsVo(String lang, String ip, String deviceId, String methodName, String requestUri, String sysType, long curTime) {
        this.lang = lang;
        this.ip = ip;
        this.deviceId = deviceId;
        this.methodName = methodName;
        this.requestUri = requestUri;
        this.sysType = sysType;
        this.curTime = curTime;
    }


    public void load(FrequencyVo frequency, String userId){
        this.frequency = frequency;
        this.userId = userId;
    }

}
