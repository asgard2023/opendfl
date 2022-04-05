package org.ccs.opendfl.core.constants;

/**
 * 频率限制类型
 */
public enum FreqLimitType {
    /**
     * 同用户次数频率
     */
    WHITE_IP(1, "whiteIp", WhiteBlackType.WHITE),
    /**
     * 同用户次数频率
     */
    WHITE_USER(2, "whiteUser", WhiteBlackType.WHITE),
    /**
     * 同用户次数频率
     */
    BLACK_IP(3, "blackIp", WhiteBlackType.BLACK),
    /**
     * 同用户次数频率
     */
    BLACK_USER(4, "blackUser", WhiteBlackType.BLACK),
    /**
     * 同用户次数频率
     */
    LIMIT(10, "limit", WhiteBlackType.FREQUENCY),
    /**
     * 同IP次数频率
     */
    LIMIT_IP(11, "limitIp", WhiteBlackType.FREQUENCY),
    /**
     * 同用户多IP数限制
     */
    USER_IP_COUNT(12, "userIp", WhiteBlackType.FREQUENCY),
    /**
     * 同IP多用户数限制
     */
    IP_USER_COUNT(13, "ipUser", WhiteBlackType.FREQUENCY);
    /**
     * IP限制的redis的zset 超限个数，以提高性能
     */
    public static final int REDIS_SET_OUT_LIMIT=2;

    private Integer type;
    private String code;
    private  WhiteBlackType whiteBlackType;

    FreqLimitType(Integer type, String code, WhiteBlackType whiteBlackType) {
        this.type = type;
        this.code = code;
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

    public Integer getType() {
        return type;
    }


    public String getCode() {
        return code;
    }

    public WhiteBlackType getWhiteBlackType(){
        return whiteBlackType;
    }
}
