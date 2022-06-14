package org.ccs.opendfl.core.constants;

/**
 * 频率限制类型
 *
 * @author chenjh
 */
public enum FreqLimitType {
    /**
     * 同用户次数频率
     * 限制数=frequency.limit
     */
    LIMIT(20, "limit", "同用户次数频率", true),
    /**
     * 同IP次数频率
     * 限制数=frequency.limit*2，以免有用户共用IP
     */
    LIMIT_IP(21, "limitIp", "同IP次数频率", true),
    /**
     * 同用户多IP数限制
     * 限制数=frequency.userIpCount
     */
    USER_IP_COUNT(22, "userIp", "同用户多IP数限制", false),
    /**
     * 同IP多用户数限制
     * 限制数=frequency.ipUserCount
     */
    IP_USER_COUNT(23, "ipUser", "同IP多用户数限制", false);
    /**
     * IP限制的redis的zset 超限个数，以提高性能
     */
    public static final int REDIS_SET_OUT_LIMIT = 2;

    private Integer type;
    private String code;
    private String typeName;
    private boolean resource;

    FreqLimitType(Integer type, String code, String typeName, boolean resource) {
        this.type = type;
        this.code = code;
        this.typeName = typeName;
        this.resource = resource;
    }

    public static FreqLimitType parse(Integer type) {
        if (type == null) {
            return null;
        }
        FreqLimitType[] types = FreqLimitType.values();
        for (FreqLimitType limitType : types) {
            if (limitType.type.intValue() == type.intValue()) {
                return limitType;
            }
        }
        return null;
    }

    public static FreqLimitType parseCode(String code) {
        if (code == null) {
            return null;
        }
        FreqLimitType[] types = FreqLimitType.values();
        for (FreqLimitType limitType : types) {
            if (limitType.code.equals(code)) {
                return limitType;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getTypeName() {
        return this.typeName;
    }


    public String getCode() {
        return code;
    }

    public boolean isResource() {
        return resource;
    }
}
