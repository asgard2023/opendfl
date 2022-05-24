package org.ccs.opendfl.mysql.constant;

/**
 * 黑白名单限制类型
 *
 * @author chenjh
 */
public enum BlackWhiteType {
    /**
     * 用户ID
     */
    BLACK(1, "black"),
    /**
     * 用户IP
     */
    WHITE(2, "white"),
    /**
     * 全局用户白名单
     */
    WHITE_GLOBAL(3, "whiteGlobal");
    private final Integer type;
    private final String code;

    BlackWhiteType(Integer type, String code) {
        this.type = type;
        this.code = code;
    }

    public Integer getType() {
        return this.type;
    }

    public String getCode() {
        return this.code;
    }
}
