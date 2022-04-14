package org.ccs.opendfl.core.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于easyui的下拉框
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComboxItemVo {
    private String id;
    private String text;
    private boolean selected;
}
