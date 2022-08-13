package org.ccs.opendfl.core.config.vo;

import lombok.Data;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.FrequencyConstant;

@Data
public class LimitFrequencyConfigVo {
    private String name;
    private Integer time;
    private Integer limit=0;
    private FreqLimitType freqLimitType;
    private String attrName="";
    private String errMsg= FrequencyConstant.FREQ_ERR_MSG;
    private String errMsgEn=FrequencyConstant.FREQ_ERR_MSG_EN;
    private String whiteCode=FrequencyConstant.NONE;

    public void setFreqLimitType(String freqLimitType){
        this.freqLimitType=FreqLimitType.parseCode(freqLimitType);
    }
}
