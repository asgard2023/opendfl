package org.ccs.opendfl.mysql.dflcore.vo;

import lombok.Data;

import java.util.Date;

/**
 * 统计频率限制数量
 *
 * @author chenjh
 */
@Data
public class DflFrequencyConfigCountVo {
    private String uri;
    private String code;
    private Integer uriId;
    private Integer cout;
    private Date maxCreateTime;
    private Date maxModifyTime;
}
