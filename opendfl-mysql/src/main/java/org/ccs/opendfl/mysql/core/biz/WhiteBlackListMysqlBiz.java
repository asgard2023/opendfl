package org.ccs.opendfl.mysql.core.biz;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IWhiteBlackListBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.mysql.constant.BlackWhiteType;
import org.ccs.opendfl.mysql.dflcore.biz.IDflBlackWhiteItemBiz;
import org.ccs.opendfl.mysql.dflcore.vo.DflBlackWhiteVo;
import org.ccs.opendfl.mysql.dflsystem.constant.SystemConfigCodes;
import org.ccs.opendfl.mysql.dflsystem.utils.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 黑白名单管理
 * 缓存10秒
 *
 * @author chenjh
 */
@Slf4j
@Service(value = "whiteBlackListMysqlBiz")
public class WhiteBlackListMysqlBiz implements IWhiteBlackListBiz {

    @Autowired
    private FrequencyConfiguration frequencyConfiguration;
    @Autowired
    private IDflBlackWhiteItemBiz dflBlackWhiteItemBiz;

    private Map<String, WhiteBlackConfigVo> configMap = new ConcurrentHashMap<>();
    private Map<String, Long> loadTimeMap = new ConcurrentHashMap<>();

    /**
     * 全局黑名单
     *
     * @return WhiteBlackConfigVo
     */
    @Override
    public WhiteBlackConfigVo getBlackConfig() {
        BlackWhiteType blackWhiteType = BlackWhiteType.BLACK;
        return getConfigCached(blackWhiteType);
    }

    @Override
    public void loadInit() {
        long time = System.currentTimeMillis();
        this.getBlackConfig();
        this.getWhiteConfig();
        this.getWhiteCodeUsers();
        log.info("----loadInit--whiteblacklist loadTime={}", System.currentTimeMillis() - time);
    }

    private WhiteBlackConfigVo getConfigCached(BlackWhiteType blackWhiteType) {
        String key = blackWhiteType.getCode();
        String keyMaxTime = blackWhiteType.getCode() + ":maxTime";
        Long curTime = System.currentTimeMillis();
        WhiteBlackConfigVo blackConfig = configMap.get(key);
        Long lastTime = loadTimeMap.get(key);
        if (blackConfig == null || lastTime == null || curTime - lastTime > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            loadTimeMap.put(key, curTime);
            Long maxTimeLast = loadTimeMap.get(keyMaxTime);
            Long maxUpdateTime = dflBlackWhiteItemBiz.findBlackWhiteMaxUpdateTime(blackWhiteType.getCode(), null);
            if (blackConfig == null || maxTimeLast.longValue() != maxUpdateTime) {
                loadTimeMap.put(keyMaxTime, maxUpdateTime);
                blackConfig = getBlackWhiteConfigLoad(blackWhiteType);
                //如果系统参数未初始化完成，读到的是参数默认值，不缓存本次结果，等初始化完成了再缓存
                boolean isInit = SystemConfig.isInit();
                log.info("-----getConfigCached--blackType={} isInit={} maxUpdateTime={} reload by modify_time changed loadTime={}", blackWhiteType.getCode(), isInit, maxUpdateTime, System.currentTimeMillis() - curTime);
                if (isInit) {
                    configMap.put(key, blackConfig);
                }
            }
        }
        return blackConfig;
    }

    private WhiteBlackConfigVo getBlackWhiteConfigLoad(BlackWhiteType blackWhiteType) {
        WhiteBlackConfigVo blackConfig = new WhiteBlackConfigVo();
        BlackWhiteType blackType = blackWhiteType;
        List<DflBlackWhiteVo> itemList = dflBlackWhiteItemBiz.findBlackWhiteList(blackType.getCode(), null);
        String users = getBlackWhiteTypeString(blackType, WhiteBlackCheckType.USER, itemList);
        blackConfig.setUsers(users);

        String ips = getBlackWhiteTypeString(blackType, WhiteBlackCheckType.IP, itemList);
        blackConfig.setIps(ips);

        String origins = getBlackWhiteTypeString(blackType, WhiteBlackCheckType.ORIGIN, itemList);
        blackConfig.setOrigins(origins);

        String deviceIds = getBlackWhiteTypeString(blackType, WhiteBlackCheckType.DEVICE, itemList);
        blackConfig.setDeviceIds(deviceIds);
        String ruleItems = null;
        if (blackWhiteType == BlackWhiteType.BLACK) {
//            Character ifDeviceIdRequire = frequencyConfiguration.getBlack().getIfDeviceIdRequire();
//            String ruleItems = frequencyConfiguration.getBlack().getItems();
            Character ifDeviceIdRequire = getCharset(SystemConfig.getByCache(SystemConfigCodes.BLACKLIST_IF_DEVICE_REQUIRE));
            ruleItems = SystemConfig.getByCache(SystemConfigCodes.BLACKLIST_RULE_ITEMS);
            blackConfig.setIfDeviceIdRequire(ifDeviceIdRequire);
            blackConfig.setItems(ruleItems);
            frequencyConfiguration.setBlack(blackConfig);
        } else if (blackWhiteType == BlackWhiteType.WHITE) {
//            Character ifDeviceIdRequire = frequencyConfiguration.getWhite().getIfDeviceIdRequire();
//            String ruleItems = frequencyConfiguration.getWhite().getItems();
            Character ifDeviceIdRequire = getCharset(SystemConfig.getByCache(SystemConfigCodes.WHITELIST_IF_DEVICE_REQUIRE));
            ruleItems = SystemConfig.getByCache(SystemConfigCodes.WHITELIST_RULE_ITEMS);
            blackConfig.setIfDeviceIdRequire(ifDeviceIdRequire);
            blackConfig.setItems(ruleItems);
            frequencyConfiguration.setWhite(blackConfig);
        }
        log.info("-----getBlackWhiteConfigLoad--blackWhiteType={} ruleItems={} users={} ips={} deviceIds={}", blackWhiteType, ruleItems, users, ips, deviceIds);
        return blackConfig;
    }

    private Character getCharset(Integer v) {
        String str = "" + v;
        return str.charAt(0);
    }

    private String getBlackWhiteTypeString(BlackWhiteType blackType, WhiteBlackCheckType limitType) {
        List<DflBlackWhiteVo> itemList = dflBlackWhiteItemBiz.findBlackWhiteList(blackType.getCode(), limitType.getType());
        return itemList.stream().map(DflBlackWhiteVo::getDatas).collect(Collectors.joining(",")) + ",";
    }

    private String getBlackWhiteTypeString(BlackWhiteType blackType, WhiteBlackCheckType limitType, List<DflBlackWhiteVo> itemList) {
        return itemList.stream().filter(t -> t.getLimitType() == limitType.getType().intValue()).map(DflBlackWhiteVo::getDatas).collect(Collectors.joining(",")) + ",";
    }

    /**
     * 全局白名单
     *
     * @return WhiteBlackConfigVo
     */
    @Override
    public WhiteBlackConfigVo getWhiteConfig() {
        BlackWhiteType blackWhiteType = BlackWhiteType.WHITE;
        return getConfigCached(blackWhiteType);
    }

    /**
     * 功能用户白名单
     *
     * @return Map<String, String> whiteCode: users
     */
    @Override
    public Map<String, String> getWhiteCodeUsers() {
        return this.frequencyConfiguration.getWhiteCodeUsers();
    }


}
