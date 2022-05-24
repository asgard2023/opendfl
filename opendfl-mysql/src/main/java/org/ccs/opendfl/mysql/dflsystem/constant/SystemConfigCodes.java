package org.ccs.opendfl.mysql.dflsystem.constant;

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
    GLOBAL_DEFAULT_ATTR_NAME("global:defaultAttrName", "全局参数默认附加属性", ConfigValueType.STRING, "userId"),
    LOCK_IF_ACTIVE("lock:ifActive", "分布式锁功能是否启用", ConfigValueType.INT, "1"),
    LOCK_REDIS_PREFIX("lock:redisPrefix", "分布式锁redisKey前缀", ConfigValueType.STRING, "limitLock"),
    FREQUENCY_REDIS_PREFIX("frequency:redisPrefix", "频率限制redisKey前缀", ConfigValueType.STRING, "limitFreq"),
    FREQUENCY_INIT_LOG_DEBUG_COUNT("frequency:initLogDebugCount", "启动时前面部份多输出部份debug日志", ConfigValueType.INT, "100"),
    FREQUENCY_MIN_RUN_TIME("frequency:minRunTime", "统计运行时间最小值(ms)", ConfigValueType.INT, "500"),
    /**
     * 比如低于5秒的超限信息，不日志，以减少常规日志量
     */
    FREQUENCY_OUT_LIMIT_MIN_TIME("frequency:outLimitMinTime", "频率限制日志记录间格时间最小值", ConfigValueType.INT, "3600"),

    BLACKLIST_IF_ACTIVE("blacklist:ifActive", "黑名单是否启用", ConfigValueType.INT, "1"),
    BLACKLIST_RULE_ITEMS("blacklist:ruleItems", "黑名单策略", ConfigValueType.STRING, "blackIp,blackUser,blackDeviceId,"),
    BLACKLIST_IF_DEVICE_REQUIRE("blacklist:ifDeviceRequire", "黑名单是否必须设备号", ConfigValueType.INT, "1"),

    WHITELIST_IF_ACTIVE("whitelist:ifActive", "白名单是否启用", ConfigValueType.INT, "1"),
    WHITELIST_RULE_ITEMS("blacklist:ruleItems", "白名单策略", ConfigValueType.STRING, "whiteIp,whiteUser,"),
    WHITELIST_IF_DEVICE_REQUIRE("whitelist:ifDeviceRequire", "白名单是否必须设备号", ConfigValueType.INT, "1");

    public static final Integer PARENT_ID_ROOT = 0;
    public static final Integer PARENT_ID_LOCK = 2;
    public static final Integer PARENT_ID_FREQUENCY = 3;
    public static final Integer PARENT_ID_BLACK = 4;
    public static final Integer PARENT_ID_WHITE = 5;

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

    SystemConfigCodes(String code, String name, ConfigValueType valueType, String defaultValue) {
        this.code = code;
        this.name = name;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
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
}
