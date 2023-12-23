package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.FrequencyType;
import org.ccs.opendfl.core.limitfrequency.Frequency;

import java.util.Objects;

@Data
public class FrequencyVo {
    private final String name;
    private final String aliasName;
    private String requestUri;
    private String method;
    private final String limitType;
    private final int time;
    private int limit = 0;
    private final FreqLimitType freqLimitType;
    private final String attrName;
    private String whiteCode;
    private String errMsg;
    private String errMsgEn;
    private boolean sysconfig;
    private boolean needLogin;
    /**
     * 是否显示日志
     *
     * @return
     */
    private boolean log;
    private Long createTime;

    public static FrequencyVo toFrequencyVo(LimitUriConfigVo uriConfigVo) {
        if (uriConfigVo == null) {
            return null;
        }
        return new FrequencyVo(uriConfigVo);
    }

    public FrequencyVo(String code, int time, FreqLimitType freqLimitType, String limitType, String aliasName, String attrName) {
        this.name = code;
        this.time = time;

        this.limitType = limitType;

        this.aliasName = aliasName;
        this.freqLimitType = freqLimitType;
        this.errMsg = null;
        this.errMsgEn = null;
        this.whiteCode = null;
        this.attrName = attrName;
        this.log = false;
    }

    /**
     * 用于Controller查询
     */
    public FrequencyVo(){
        this(new LimitUriConfigVo());
    }

    public FrequencyVo(LimitUriConfigVo uriConfigVo) {
        this.limitType = FrequencyType.URI_CONFIG.getType();
        this.method = uriConfigVo.getMethod();
        this.name = uriConfigVo.getUri();
        this.aliasName = uriConfigVo.getAliasName();
        if(uriConfigVo.getTime()!=null) {
            this.time = uriConfigVo.getTime();
        }
        else{
            this.time=0;
        }
        this.limit = uriConfigVo.getLimit();
        this.freqLimitType = uriConfigVo.getFreqLimitType();
        this.errMsg = uriConfigVo.getErrMsg();
        this.errMsgEn = uriConfigVo.getErrMsgEn();
        this.whiteCode = uriConfigVo.getWhiteCode();
        this.attrName = uriConfigVo.getAttrName();
        this.log = uriConfigVo.isLog();
    }


    public static FrequencyVo toFrequencyVo(Frequency frequency) {
        if (frequency == null) {
            return null;
        }
        return new FrequencyVo(frequency);
    }

    /**
     * @param frequency 频率限制
     * @return 转vo对应
     */
    public FrequencyVo(Frequency frequency) {
        this.limitType = FrequencyType.FREQUENCY.getType();
        this.name = frequency.name();
        this.aliasName = frequency.aliasName();
        this.time = frequency.time();
        this.limit = frequency.limit();
        this.freqLimitType = frequency.freqLimitType();
        this.errMsg = frequency.errMsg();
        this.errMsgEn = frequency.errMsgEn();
        this.whiteCode = frequency.whiteCode();
        this.attrName = frequency.attrName();
        this.sysconfig = frequency.sysconfig();
        this.needLogin = frequency.needLogin();
        this.log = frequency.log();
    }

    public FrequencyVo(FrequencyVo frequency) {
        this.limitType = FrequencyType.FREQUENCY5.getType();
        this.name = frequency.getName();
        this.aliasName = frequency.getAliasName();
        this.time = frequency.getTime();
        this.limit = frequency.getLimit();
        this.freqLimitType = frequency.getFreqLimitType();
        this.errMsg = frequency.getErrMsg();
        this.errMsgEn = frequency.getErrMsgEn();
        this.whiteCode = frequency.getWhiteCode();
        this.attrName = frequency.getAttrName();
        this.sysconfig = frequency.isSysconfig();
        this.needLogin = frequency.isNeedLogin();
        this.log = frequency.isLog();

        this.requestUri = frequency.getRequestUri();
        this.method = frequency.getMethod();
    }


    @Override
    public String toString() {
        return "FrequencyVo{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", limit=" + limit +
                ", freqLimitType=" + (freqLimitType != null ? freqLimitType.getCode() : "") +
//                ", pageReachLog=" + pageReachLog +
                ", attrName='" + attrName + '\'' +
                ", whiteCode=" + whiteCode +
                ", errMsg='" + errMsg + '\'' +
                ", errMsgEn='" + errMsgEn + '\'' +
                ", sysconfig=" + sysconfig +
                ", log=" + log +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time, limit, freqLimitType.getType(), attrName, errMsg, errMsgEn, log);
    }


    public FrequencyVo toCopy() {
        return new FrequencyVo(this);
    }
}
