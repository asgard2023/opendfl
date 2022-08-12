package org.ccs.opendfl.mysql.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.vo.FrequencyVo;

import java.io.Serializable;

@Data
public class FrequencyMysqlVo extends FrequencyVo implements Serializable {
    private Integer status;

    public static FrequencyMysqlVo copy(FrequencyVo frequencyVo) {
        FrequencyMysqlVo frequencyMysqlVo = new FrequencyMysqlVo();
        frequencyMysqlVo.setTime(frequencyVo.getTime());
        frequencyMysqlVo.setLimit(frequencyVo.getLimit());
        frequencyMysqlVo.setAttrName(frequencyVo.getAttrName());
        frequencyMysqlVo.setName(frequencyVo.getName());
        frequencyMysqlVo.setAliasName(frequencyVo.getAliasName());
        frequencyMysqlVo.setErrMsg(frequencyVo.getErrMsg());
        frequencyMysqlVo.setErrMsgEn(frequencyVo.getErrMsgEn());
        frequencyMysqlVo.setFreqLimitType(frequencyVo.getFreqLimitType());
        frequencyMysqlVo.setNeedLogin(frequencyVo.isNeedLogin());
        frequencyMysqlVo.setRequestUri(frequencyVo.getRequestUri());
        frequencyMysqlVo.setWhiteCode(frequencyVo.getWhiteCode());
        frequencyMysqlVo.setLimitType(frequencyVo.getLimitType());
        return frequencyMysqlVo;
    }
}
