package org.ccs.opendfl.core.constants;

/**
 * 超限类型
 *
 * @author chenjh
 */
public enum OutLimitType {
    /**
     * 频率限制
     */
    FREQUENCY(1, "frequency"),
    /**
     * 白名单
     */
    WHITE(2, "white"),
    /**
     * 黑名单
     */
    BLACK(3, "black");

    private final Integer type;
    private final String code;

    OutLimitType(Integer type, String code) {
        this.type = type;
        this.code = code;
    }


    public Integer getType() {
        return this.type;
    }

    public static OutLimitType parseCode(String code) {
        if (code == null) {
            return null;
        }
        OutLimitType[] types = OutLimitType.values();
        for (OutLimitType limitType : types) {
            if (limitType.code.equals(code)) {
                return limitType;
            }
        }
        return null;
    }
}
