package org.ccs.opendfl.core.config.vo;

import lombok.Data;

import java.util.List;

/**
 * 频率限制配置
 *
 * @author chenjh
 */
@Data
public class LimitConfigVo {
    private String items;
    private String resourceLimitType="ip,data";
    private Integer outLimitLogTime=3600;
    private List<LimitFrequencyConfigVo> frequencyConfigs;
    private List<LimitUriConfigVo> uriConfigs;
}
