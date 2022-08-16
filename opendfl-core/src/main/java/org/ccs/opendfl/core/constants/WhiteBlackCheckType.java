package org.ccs.opendfl.core.constants;

/**
 * 黑名单，白名单检查类型
 *
 * @author chenjh
 */
public enum WhiteBlackCheckType {
    /**
     * 用户
     */
    USER(1, "user", "用户"),
    /**
     * IP
     */
    IP(2, "ip", "IP"),
    /**
     * 设备号
     */
    DEVICE(3, "device", "设备号"),
    /**
     * header origin
     */
    ORIGIN(4, "origin", "origin"),
    /**
     * header type_token
     */
    TYPE_TOKEN(5, "type_token", "type_token");

    private final Integer type;
    private final String code;
    private final String typeName;

    WhiteBlackCheckType(Integer type, String code, String typeName) {
        this.type = type;
        this.code = code;
        this.typeName = typeName;
    }

    public Integer getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getTypeName() {
        return this.typeName;
    }


    public static WhiteBlackCheckType parse(Integer type) {
        if (type == null) {
            return null;
        }
        WhiteBlackCheckType[] types = WhiteBlackCheckType.values();
        for (WhiteBlackCheckType limitType : types) {
            if (limitType.type.intValue() == type.intValue()) {
                return limitType;
            }
        }
        return null;
    }

    public static WhiteBlackCheckType parseCode(String code) {
        if (code == null) {
            return null;
        }
        WhiteBlackCheckType[] types = WhiteBlackCheckType.values();
        for (WhiteBlackCheckType limitType : types) {
            if (limitType.code.equals(code)) {
                return limitType;
            }
        }
        return null;
    }
}
