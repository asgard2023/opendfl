package org.ccs.opendfl.core.config.vo;

import lombok.Data;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.FrequencyConstant;

@Data
public class LimitUriConfigVo {
    private String uri;
    private String method;
    private String aliasName;
    private Integer time;
    private Integer limit = 0;
    private FreqLimitType freqLimitType;
    private String attrName = "";
    private Integer status=0;
    /**
     * 是否显示日志
     * @return
     */
    private boolean log;
    private boolean needLogin;
    private String errMsg = FrequencyConstant.FREQ_ERR_MSG;
    private String errMsgEn = FrequencyConstant.FREQ_ERR_MSG_EN;
    private String whiteCode = FrequencyConstant.NONE;
}
