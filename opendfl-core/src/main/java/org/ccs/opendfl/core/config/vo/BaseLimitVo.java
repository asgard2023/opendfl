package org.ccs.opendfl.core.config.vo;

import lombok.Data;

/**
 * 基本限制
 */
@Data
public class BaseLimitVo {
    private int pageNumMax=1000;
    private int pageSizeMax=1000;
    private int totalRowMax=100000;
    private int searchDateMaxDay=90;
}
