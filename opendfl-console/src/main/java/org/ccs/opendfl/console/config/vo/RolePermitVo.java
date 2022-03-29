package org.ccs.opendfl.console.config.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermitVo{
    private String role;
    private Integer ifView=0;
    private Integer ifClear=0;
    private Integer ifEvict=0;
}
