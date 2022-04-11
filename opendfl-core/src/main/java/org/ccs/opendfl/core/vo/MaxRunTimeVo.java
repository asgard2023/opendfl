package org.ccs.opendfl.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaxRunTimeVo {
    private String uri;
    private Long createTime;
    private Long maxRunTime;
}
