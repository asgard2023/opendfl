package org.ccs.opendfl.core.config.vo;

import lombok.Data;

import java.util.List;

@Data
public class LimitConfigVo {
    private String items;
    private String resourceLimitType="ip,data";
    private Character ipLimitSplitFunction='0';
    private Integer outLimitLogTime=3600;
    private List<LimitFrequencyConfigVo> frequencyConfigs;
    private List<LimitUriConfigVo> uriConfigs;
}
