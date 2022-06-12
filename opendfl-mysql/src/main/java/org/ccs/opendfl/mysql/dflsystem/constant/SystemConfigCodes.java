package org.ccs.opendfl.mysql.dflsystem.constant;

import org.ccs.opendfl.core.utils.CommUtils;

import java.util.Arrays;

/**
 * 系统参数枚举
 * <p>
 * 当首次调用时，可自动写表(DflSystemConfig)
 * 这里的值只是初始值，值后面通过配置功能发生调整，与这里无关
 * 如果数据状态无效，仍可用这里的值做为默认值
 *
 * @author chenjh
 */
public enum SystemConfigCodes {
    /**
     * 全局参数默认附加属性
     */
    GLOBAL_DEFAULT_ATTR_NAME("global:defaultAttrName", "全局参数默认附加属性", ConfigValueType.STRING, "userId", Constants.PID_BASE_LIMIT),
    /**
     * opendfl.baseLimit
     */
    BASE_LIMIT_PAGE_NUM_MAX("baseLimit:pageNumMax", "baseLimit最大页数", ConfigValueType.INT, "1000", Constants.PID_BASE_LIMIT),
    BASE_LIMIT_PAGE_SIZE_MAX("baseLimit:pageSizeMax", "baseLimit最大每页行数", ConfigValueType.INT, "1000", Constants.PID_BASE_LIMIT),
    BASE_LIMIT_TOTAL_ROW_MAX("baseLimit:totalRowMax", "baseLimit最大总行数", ConfigValueType.INT, "10000", Constants.PID_BASE_LIMIT),
    BASE_LIMIT_SEARCH_DATE_DAY_MAX("baseLimit:searchDateDayMax", "baseLimit查询时段最大天数", ConfigValueType.INT, "90", Constants.PID_BASE_LIMIT),

    /**
     * 分布式锁功能是否启用
     */
    LOCK_IF_ACTIVE("lock:ifActive", "分布式锁功能是否启用", ConfigValueType.INT, "1", Constants.PID_LOCK),
    /**
     * 分布式锁redisKey前缀
     */
    LOCK_REDIS_PREFIX("lock:redisPrefix", "分布式锁redisKey前缀", ConfigValueType.STRING, "limitLock", Constants.PID_LOCK),

    /**
     * 频率限制redisKey前缀
     */
    FREQUENCY_REDIS_PREFIX("frequency:redisPrefix", "频率限制redisKey前缀", ConfigValueType.STRING, "limitFreq", Constants.PID_FREQUENCY),
    /**
     * 是否支持接口监控，允许monitor接口返回最近接口执行时间
     */
    FREQUENCY_RUN_TIME_MONITOR("frequency:runTimeMonitor", "频率限制是否支持接口监控", ConfigValueType.INT, "1", Constants.PID_FREQUENCY),
    /**
     * 启动时前面部份多输出部份debug日志
     */
    FREQUENCY_INIT_LOG_DEBUG_COUNT("frequency:initLogDebugCount", "启动时前面部份多输出部份debug日志", ConfigValueType.INT, "100", Constants.PID_FREQUENCY),
    /**
     * 记录慢接口时间(毫秒)，为0关闭此功能
     */
    FREQUENCY_MIN_RUN_TIME("frequency:minRunTime", "统计运行时间最小值(ms)", ConfigValueType.INT, "500", Constants.PID_FREQUENCY),
    /**
     * 比如低于5秒的超限信息，不日志，以减少常规日志量
     */
    LIMIT_OUT_LIMIT_MIN_TIME("limit:outLimitMinTime", "频率限制日志记录间格时间最小值", ConfigValueType.INT, "3600", Constants.PID_FREQUENCY),
    LIMIT_RULE_ITEMS("limit:ruleItems", "频率限制策略", ConfigValueType.STRING, "limit,userCount,userIp,ipUser,", Constants.PID_FREQUENCY),
    LIMIT_IP_SPLIT("limit:ipLimitSplitFunction", "IP限制是否区分功能，如果不区分功能，则缓存有效期相同，以及ip数或用户数共享", ConfigValueType.INT, "0", Constants.PID_FREQUENCY),


    BLACKLIST_IF_ACTIVE("blacklist:ifActive", "黑名单是否启用", ConfigValueType.INT, "1", Constants.PID_BLACK),
    BLACKLIST_RULE_ITEMS("blacklist:ruleItems", "黑名单策略", ConfigValueType.STRING, "blackIp,blackUser,blackDeviceId,", Constants.PID_BLACK),
    BLACKLIST_IF_DEVICE_REQUIRE("blacklist:ifDeviceRequire", "黑名单是否必须设备号", ConfigValueType.INT, "1", Constants.PID_BLACK),


    WHITELIST_IF_ACTIVE("whitelist:ifActive", "白名单是否启用", ConfigValueType.INT, "1", Constants.PID_WHITE),
    WHITELIST_RULE_ITEMS("whitelist:ruleItems", "白名单策略", ConfigValueType.STRING, "whiteIp,whiteUser,", Constants.PID_WHITE),
    WHITELIST_IF_DEVICE_REQUIRE("whitelist:ifDeviceRequire", "白名单是否必须设备号", ConfigValueType.INT, "1", Constants.PID_WHITE),
    WHITELIST_IF_ORIGIN_REQUIRE("whitelist:ifOriginRequire", "白名单是否必须origin", ConfigValueType.INT, "0", Constants.PID_WHITE);

    private static class Constants {
        public static final Integer PID_ROOT = 0;
        public static final Integer PID_LOCK = 2;
        public static final Integer PID_FREQUENCY = 3;
        public static final Integer PID_BLACK = 4;
        public static final Integer PID_WHITE = 5;
        public static final Integer PID_BASE_LIMIT = 6;
        public static final String pids= CommUtils.concat(Arrays.asList(PID_ROOT, PID_LOCK, PID_FREQUENCY, PID_BLACK, PID_WHITE, PID_BASE_LIMIT), ",")+",";
    }

    public static boolean isPid(Integer id){
        return Constants.pids.contains(id+",");
    }


    public static SystemConfigCodes parse(String code) {
        SystemConfigCodes[] arrays = SystemConfigCodes.values();
        for (SystemConfigCodes obj : arrays) {
            if (obj.code.equals(code)) {
                return obj;
            }
        }
        return null;
    }

    private final String code;
    private final String name;
    private final ConfigValueType valueType;
    private final String defaultValue;
    private final Integer parentId;

    SystemConfigCodes(String code, String name, ConfigValueType valueType, String defaultValue, Integer parentId) {
        this.code = code;
        this.name = name;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getName() {
        return name;
    }

    public ConfigValueType getValueType() {
        return valueType;
    }

    public Integer getParentId() {
        return parentId;
    }
}
