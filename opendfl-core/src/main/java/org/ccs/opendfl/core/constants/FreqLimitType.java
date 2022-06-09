package org.ccs.opendfl.core.constants;

/**
 * 频率限制类型
 */
public enum FreqLimitType {
    /**
     * 同用户次数频率
     */
    LIMIT(20, "limit", "同用户次数频率", WhiteBlackType.FREQUENCY),
    /**
     * 同IP次数频率
     */
    LIMIT_IP(21, "limitIp", "同IP次数频率", WhiteBlackType.FREQUENCY),
    /**
     * 同用户多IP数限制
     */
    USER_IP_COUNT(22, "userIp", "同用户多IP数限制", WhiteBlackType.FREQUENCY),
    /**
     * 同IP多用户数限制
     */
    IP_USER_COUNT(23, "ipUser", "同IP多用户数限制", WhiteBlackType.FREQUENCY);
    /**
     * IP限制的redis的zset 超限个数，以提高性能
     */
    public static final int REDIS_SET_OUT_LIMIT = 2;

    private Integer type;
    private String code;
    private String typeName;
    private WhiteBlackType whiteBlackType;

    FreqLimitType(Integer type, String code, String typeName, WhiteBlackType whiteBlackType) {
        this.type = type;
        this.code = code;
        this.typeName = typeName;
        this.whiteBlackType = whiteBlackType;
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

    public WhiteBlackType getWhiteBlackType() {
        return whiteBlackType;
    }
}
