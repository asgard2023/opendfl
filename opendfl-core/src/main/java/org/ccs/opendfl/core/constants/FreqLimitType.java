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
    LIMIT(1, "limit", "同用户次数频率", false),
    /**
     * 同IP次数频率
     * 限制数=frequency.limit*{limitIpRate}，以免有用户共用IP
     */
    LIMIT_IP(2, "limitIp", "同IP次数频率", false),
    /**
     * 同用户多IP数限制
     * 限制数=frequency.userIpCount
     */
    USER_IP(3, "userIp", "同用户多IP数限制", false),
    /**
     * 同IP多用户数限制
     * 限制数=frequency.ipUserCount
     */
    IP_USER(4, "ipUser", "同IP多用户数限制", false),
    /**
     * 同资源ID同IP访问次数限制
     */
    RES_IP(5, "resIp", "同资源ID同IP访问次数限制", true),
    /**
     * 同资源ID同用户访问次数限制
     */
    RES_USER(6, "resUser", "同资源ID用户访问次数限制", true);

    /**
     * IP限制的redis的zset 超限个数，允许多存2个ip
     */
    public static final int REDIS_SET_OUT_LIMIT = 2;

    private final Integer type;
    private final String code;
    private final String typeName;
    private final boolean resource;

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
