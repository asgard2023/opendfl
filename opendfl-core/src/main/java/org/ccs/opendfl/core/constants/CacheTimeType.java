package org.ccs.opendfl.core.constants;

/**
 * Redis缓存时间间隔类型
 * @author chenjh
 */
public enum CacheTimeType {
    /**
     * 默认10分钟
     */
    CACHE_DEFAULT(CacheTimeType.DEFAULT, 600, "10分钟"),
    /**
     * 30秒
     */
    CACHE_30_SEC(CacheTimeType.CACHE30S, 30, "30秒"),
    /**
     * 缓存1小时
     */
    CACHE_1_HOUR(CacheTimeType.CACHE1H, 3600, "1小时"),
    /**
     * 缓存1天
     */
    CACHE_1_DAY(CacheTimeType.CACHE1D, 86400, "1天"),
    /**
     * 缓存一周
     */
    CACHE_1_WEEK(CacheTimeType.CACHE1W, 86400 * 7, "1周");
    public static final String DEFAULT="default";
    public static final String CACHE30S="cache30s";
    public static final String CACHE1H="cache1h";
    public static final String CACHE1D="cache1d";
    public static final String CACHE1W="cache1w";
    private final String code;
    private final int second;
    private final String desc;

    CacheTimeType(String code, int second, String desc) {
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