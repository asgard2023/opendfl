package org.ccs.opendfl.mysql.constant;

/**
 * 状态
 *
 * @author chenjh
 */
public enum CommonStatus {
    /**
     * 有效
     */
    VALID(1, "ok"),
    /**
     * 无效
     */
    INVALID(0, "invalid");
    private final int status;
    private final String code;

    CommonStatus(Integer status, String code) {
        this.status = status;
        this.code = code;
    }

    public int getStatus() {
        return this.status;
    }

    public String getCode() {
        return this.code;
    }
}
