package org.ccs.opendfl.mysql.dflsystem.utils;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflSystemConfigBiz;
import org.ccs.opendfl.mysql.dflsystem.constant.ConfigValueType;
import org.ccs.opendfl.mysql.dflsystem.constant.SystemConfigCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统参数读取（本地缓存10秒）
 * 如果参数数据不存在或已删除，会自动将参数保存到数据库
 * 如果参数数据状态无效，则取初始默认值
 * 如果参数数据状态有效，则返回对应的参数值
 *
 * @author chenjh
 */
@Slf4j
@Service
public class SystemConfig {

    private static IDflSystemConfigBiz dflSystemConfigBiz;

    @Autowired
    public void setDflSystemConfigBiz(IDflSystemConfigBiz dflSystemConfigBiz) {
        SystemConfig.dflSystemConfigBiz = dflSystemConfigBiz;
    }

    /**
     * 用于启动时，有些强依赖的参数检查
     *
     * @return
     */
    public static boolean isInit() {
        return dflSystemConfigBiz != null;
    }

    /**
     * 本地缓存60秒
     */
    protected static final Map<String, Object> configMap = new ConcurrentHashMap<>();
    protected static final Map<String, Long> configLoadTimeMap = new ConcurrentHashMap<>();

    public static <E> E getByCache(SystemConfigCodes sysConfigCodes) {
        return getByCache(sysConfigCodes, sysConfigCodes.getParentId());
    }


    public static <E> E getByCache(SystemConfigCodes sysConfigCodes, Integer parentId) {
        final String configCode = sysConfigCodes.getCode();
        final ConfigValueType valueType = sysConfigCodes.getValueType();
        final String defualt = sysConfigCodes.getDefaultValue();
        if (dflSystemConfigBiz == null) {
            log.warn("-----getByCache--isInit=false--configCode={} use defaultValue", configCode);
            return ConfigValueType.getValue(valueType, defualt);
        }
        String cacheKey = configCode;
        Long loadTime = configLoadTimeMap.get(cacheKey);
        Long curTime = System.currentTimeMillis();
        if (loadTime == null) {
            loadTime = 0L;
        }
        E value = (E) configMap.get(cacheKey);
        if (curTime - loadTime > FrequencyConstant.LOAD_CONFIG_INTERVAL || value == null) {
            loadTime = curTime;
            configLoadTimeMap.put(cacheKey, loadTime);
            E value2 = getByLang(configCode);
            if (value2 == null) {
                //首次未找到，则直接使用默认值
                value2 = ConfigValueType.getValue(valueType, defualt);
                //异步保存，不影响主要功能的性能
                dflSystemConfigBiz.saveSysConfigAsync(sysConfigCodes, parentId);
            }
            if (!isEqual(valueType, value, value2)) {
                value = value2;
                configMap.put(cacheKey, value);
            }
        }
        return value;
    }

    private static <E> boolean isEqual(ConfigValueType valueType, E v1, E v2) {
        if (valueType == ConfigValueType.INT) {
            Integer numV1 = (Integer) v1;
            if (numV1 == null) {
                numV1 = 0;
            }
            Integer numV2 = (Integer) v2;
            if (numV2 == null) {
                numV2 = 0;
            }
            return numV1 == numV2.intValue();
        } else {
            String str1 = (String) v1;
            String str2 = (String) v2;
            return StringUtils.equals(str1, str2);
        }
    }


    /**
     * 推荐用getByCache走本地缓存，性能更高
     *
     * @param configCode
     * @param <E>
     * @return
     */
    public static <E> E getByLang(String configCode) {
        return dflSystemConfigBiz.getConfigValue(configCode);
    }
}
