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

import javax.annotation.PostConstruct;
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

    @PostConstruct
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
                log.info("-----getConfigCached--blackType={} maxUpdateTime={} reload by modify_time changed loadTime={}", blackWhiteType.getCode(), maxUpdateTime, System.currentTimeMillis() - curTime);
                //如果系统参数未初始化完成，读到的是参数默认值，不缓存本次结果，等初始化完成了再缓存
                if (SystemConfig.isInit()) {
                    configMap.put(key, blackConfig);
                }
            }
        }
        return blackConfig;
    }

    private WhiteBlackConfigVo getBlackWhiteConfigLoad(BlackWhiteType blackWhiteType) {
        WhiteBlackConfigVo blackConfig = new WhiteBlackConfigVo();
        BlackWhiteType blackType = blackWhiteType;
        String users = getBlackWhiteTypeString(blackType, WhiteBlackCheckType.USER);
        blackConfig.setUsers(users);

        String ips = getBlackWhiteTypeString(blackType, WhiteBlackCheckType.IP);
        blackConfig.setIps(ips);

        String deviceIds = getBlackWhiteTypeString(blackType, WhiteBlackCheckType.DEVICE);
        blackConfig.setDeviceIds(deviceIds);
        log.debug("-----getBlackWhiteConfigLoad--users={} ips={} deviceIds={}", users, ips, deviceIds);
        if (blackWhiteType == BlackWhiteType.BLACK) {
//            Character ifDeviceIdRequire = frequencyConfiguration.getBlack().getIfDeviceIdRequire();
//            String ruleItems = frequencyConfiguration.getBlack().getItems();
            Character ifDeviceIdRequire = getCharset(SystemConfig.getByCache(SystemConfigCodes.BLACKLIST_IF_DEVICE_REQUIRE, SystemConfigCodes.PARENT_ID_BLACK));
            String ruleItems = SystemConfig.getByCache(SystemConfigCodes.BLACKLIST_RULE_ITEMS, SystemConfigCodes.PARENT_ID_BLACK);
            blackConfig.setIfDeviceIdRequire(ifDeviceIdRequire);
            blackConfig.setItems(ruleItems);
            frequencyConfiguration.setBlack(blackConfig);
        } else if (blackWhiteType == BlackWhiteType.WHITE) {
//            Character ifDeviceIdRequire = frequencyConfiguration.getWhite().getIfDeviceIdRequire();
//            String ruleItems = frequencyConfiguration.getWhite().getItems();
            Character ifDeviceIdRequire = getCharset(SystemConfig.getByCache(SystemConfigCodes.WHITELIST_IF_DEVICE_REQUIRE, SystemConfigCodes.PARENT_ID_WHITE));
            String ruleItems = SystemConfig.getByCache(SystemConfigCodes.WHITELIST_RULE_ITEMS, SystemConfigCodes.PARENT_ID_WHITE);
            blackConfig.setIfDeviceIdRequire(ifDeviceIdRequire);
            blackConfig.setItems(ruleItems);
            frequencyConfiguration.setWhite(blackConfig);
        }
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
