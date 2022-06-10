package org.ccs.opendfl.core.config.vo;

import lombok.Data;
import org.ccs.opendfl.core.constants.FrequencyConstant;

@Data
public class LimitUriConfigVo {
    private String uri;
    private String method;
    private String aliasName;
    private Integer time;
    private Integer limit = 0;
    private Integer userIp = 0;
    private Integer ipUser = 0;
    private boolean resource;
    private String attrName = "";
    private Integer status=0;
    private String errMsg = FrequencyConstant.FREQ_ERR_MSG;
    private String errMsgEn = FrequencyConstant.FREQ_ERR_MSG_EN;
    private String whiteCode = FrequencyConstant.NONE;
}
