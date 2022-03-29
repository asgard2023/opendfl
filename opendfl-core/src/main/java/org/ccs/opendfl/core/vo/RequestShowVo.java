package org.ccs.opendfl.core.vo;

import lombok.Data;

import java.util.List;

@Data
public class RequestShowVo extends RequestVo {
    private String limitTypes;
    private List<FrequencyVo> limitFrequencys;
    private List<RequestLockVo> locks;
}
