package org.ccs.opendfl.core.constants;

/**
 * 频率限制类型
 */
public enum FreqLimitType {
    /**
     * IP白名单
     */
    WHITE_IP(1, "whiteIp", "IP白名单", WhiteBlackType.WHITE),
    /**
     * 用户白名单
     */
    WHITE_USER(2, "whiteUser", "用户白名单", WhiteBlackType.WHITE),
    /**
     * IP黑名单
     */
    BLACK_IP(3, "blackIp", "IP黑名单", WhiteBlackType.BLACK),
    /**
     * 用户黑名单
     */
    BLACK_USER(4, "blackUser", "用户黑名单", WhiteBlackType.BLACK),
    /**
     * 设备ID黑名单
     */
    BLACK_DEVICE_ID(5, "blackDeviceId", "设备ID黑名单", WhiteBlackType.BLACK),
    /**
     * 同用户次数频率
     */
    LIMIT(10, "limit", "同用户次数频率", WhiteBlackType.FREQUENCY),
    /**
     * 同IP次数频率
     */
    LIMIT_IP(11, "limitIp", "同IP次数频率", WhiteBlackType.FREQUENCY),
    /**
     * 同用户多IP数限制
     */
    USER_IP_COUNT(12, "userIp", "同用户多IP数限制", WhiteBlackType.FREQUENCY),
    /**
     * 同IP多用户数限制
     */
    IP_USER_COUNT(13, "ipUser", "同IP多用户数限制", WhiteBlackType.FREQUENCY);
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
