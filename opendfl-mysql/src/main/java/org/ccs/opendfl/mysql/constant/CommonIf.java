package org.ccs.opendfl.mysql.constant;

/**
 * 是否
 *
 * @author chenjh
 */
public enum CommonIf {
    /**
     * 有效
     */
    YES(1, "Yes"),
    /**
     * 无效
     */
    NO(0, "no");
    private final int type;
    private final String code;

    CommonIf(Integer type, String code) {
        this.type = type;
        this.code = code;
    }

    public int getType() {
        return this.type;
    }

    public String getCode() {
        return this.code;
    }
}
