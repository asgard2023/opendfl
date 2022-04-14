package org.ccs.opendfl.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunCountVo {
    private String uri;
    private Integer count;
}
