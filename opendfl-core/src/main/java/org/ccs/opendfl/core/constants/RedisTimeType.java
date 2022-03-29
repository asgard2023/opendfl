package org.ccs.opendfl.core.constants;

public enum RedisTimeType {
    CACHE_DEFAULT(RedisTimeType.DEFAULT, 600, "10分钟"),
    CACHE_30_SEC(RedisTimeType.CACHE30S, 30, "30秒"),
    CACHE_1_HOUR(RedisTimeType.CACHE1H, 3600, "1小时"),
    CACHE_1_DAY(RedisTimeType.CACHE1D, 86400, "1天"),
    CACHE_1_WEEK(RedisTimeType.CACHE1W, 86400 * 7, "1周");
    public static final String DEFAULT="default";
    public static final String CACHE30S="cache30s";
    public static final String CACHE1H="cache1h";
    public static final String CACHE1D="cache1d";
    public static final String CACHE1W="cache1w";
    private String code;
    private int second;
    private String desc;

    RedisTimeType(String code, int second, String desc) {
        this.code = code;
        this.second = second;
        this.desc = desc;
    }

    public int getSecond() {
        return second;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}