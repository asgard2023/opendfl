package org.ccs.opendfl.core.config.vo;

import lombok.Data;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.FrequencyConstant;

/**
 * uri频率限制配置
 *
 * @author chenjh
 */
@Data
public class LimitUriConfigVo {
    private String uri;
    private String method;
    private String aliasName;
    /**
     * 间隔时间(秒)
     */
    private Integer time;
    /**
     * 限制次数
     */
    private Integer limit = 0;
    /**
     * 限制类型，默认limit
     */
    private FreqLimitType freqLimitType=FreqLimitType.LIMIT;
    private String attrName = "";
    private Integer status = 1;
    /**
     * 是否显示日志
     */
    private boolean log;
    private boolean needLogin;
    private String errMsg = FrequencyConstant.FREQ_ERR_MSG;
    private String errMsgEn = FrequencyConstant.FREQ_ERR_MSG_EN;
    private String whiteCode = FrequencyConstant.NONE;
}
