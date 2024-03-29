package org.ccs.opendfl.mysql.core.biz;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IFrequencyConfigBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.OpendflConfiguration;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.FrequencyType;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.ccs.opendfl.mysql.constant.CommonStatus;
import org.ccs.opendfl.mysql.core.vo.FrequencyMysqlVo;
import org.ccs.opendfl.mysql.dflcore.biz.IDflFrequencyBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.ccs.opendfl.mysql.dflsystem.constant.SystemConfigCodes;
import org.ccs.opendfl.mysql.dflsystem.utils.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 频率限制配置查询-redis
 * 缓存10秒
 * 如果数据无效，将不起作用，使用原来注解上的值作为默认，如果是uri配置没有注解，则没有限制
 *
 * @author chenjh
 */
@Service(value = "frequencyConfigMysqlBiz")
@Slf4j
public class FrequencyConfigMysqlBiz implements IFrequencyConfigBiz {
    @Autowired
    private IDflFrequencyBiz dflFrequencyBiz;
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;
    @Autowired
    private OpendflConfiguration opendflConfiguration;

    /**
     * 有修改日志一下
     *
     * @param frequencyNew FrequencyVo
     */
    private boolean checkChange(FrequencyMysqlVo frequencyExist, FrequencyMysqlVo frequencyNew) {
        boolean isChanged = false;
        if (frequencyExist == null || frequencyExist.hashCode() != frequencyNew.hashCode()) {
            isChanged = true;
        }
        if (isChanged) {
            FrequencyVo frequency = frequencyNew.getFrequency();
            log.info("----checkChange--name={} time={} limit={} freqLimitType={} errorMsg={}"
                    , frequency.getName(), frequency.getTime(), frequency.getLimit(), frequency.getFreqLimitType(), frequency.getErrMsg());
        }
        return isChanged;
    }

    /**
     * 主要按接口缓存，理论上接口数不会太多，用Map做持久缓存，不会占太多内存
     */
    private static Map<String, Long> loadSysconfigTimeMap = new ConcurrentHashMap<>(128);
    private static Map<String, FrequencyMysqlVo> sysconfigLimitMap = new ConcurrentHashMap<>(64);
    private static Map<String, List<LimitUriConfigVo>> uriLimitConfigsMap = new ConcurrentHashMap<>(128);

    @Override
    public void limitBySysconfigLoad(FrequencyVo frequency, Long curTime) {
        loadAnyChange(curTime);
        //只支持注解模式，uriConfig模式不处理
        if (StringUtils.equals(FrequencyType.URI_CONFIG.getType(), frequency.getLimitType())) {
            return;
        }
        int freqLimitType = frequency.getFreqLimitType().getType();
        String key = frequency.getName() + ":" + freqLimitType + ":" + frequency.getTime();
        Long time = loadSysconfigTimeMap.get(key);
        FrequencyMysqlVo frequencyExist = sysconfigLimitMap.get(key);
        //缓存10秒，10秒加载一次
        if (time == null || curTime - time > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            loadSysconfigTimeMap.put(key, curTime);
            this.recoverFrequencyConfig(curTime);
            FrequencyMysqlVo frequencyNew = limitBySysconfig(frequency);
            if (frequencyNew != null && checkChange(frequencyExist, frequencyNew)) {
                //因为FrequencyVo是可以共用的，存起来也可以发生变化，所以这里缓存时clone一下
                sysconfigLimitMap.put(key, frequencyNew);
            }
            frequencyExist = frequencyNew;
        }

        if (frequencyExist != null) {
            if (frequencyExist.getStatus() != null && frequencyExist.getStatus() == CommonStatus.VALID.getStatus()) {
                frequency.setLimit(frequencyExist.getFrequency().getLimit());
                frequency.setErrMsg(frequencyExist.getFrequency().getErrMsg());
                frequency.setErrMsgEn(frequencyExist.getFrequency().getErrMsgEn());
                frequency.setSysconfig(frequencyExist.getFrequency().isSysconfig());
            }
        }
    }

    private static Long recoverFrequencyConfigTime = 0L;

    /**
     * 把系统参数的配置覆盖到frequencyConfiguration的默认配置
     * 也做频率限制，以减少调用次数
     *
     * @param curTime
     */
    private void recoverFrequencyConfig(Long curTime) {
        if (curTime - recoverFrequencyConfigTime > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            if (recoverFrequencyConfigTime == 0) {
                log.info("-----recoverFrequencyConfig--");
            }
            recoverFrequencyConfigTime = curTime;
            try {
                Integer minRunTime = SystemConfig.getByCache(SystemConfigCodes.FREQUENCY_MIN_RUN_TIME);
                Integer runTimeMonitor = SystemConfig.getByCache(SystemConfigCodes.FREQUENCY_RUN_TIME_MONITOR);
                frequencyConfiguration.setMinRunTime(0L + minRunTime);
                frequencyConfiguration.setRedisPrefix(SystemConfig.getByCache(SystemConfigCodes.FREQUENCY_REDIS_PREFIX));
                frequencyConfiguration.setRunTimeMonitor(("" + runTimeMonitor).charAt(0));
                frequencyConfiguration.setInitLogCount(SystemConfig.getByCache(SystemConfigCodes.FREQUENCY_INIT_LOG_DEBUG_COUNT));
                String limitIpRate = SystemConfig.getByCache(SystemConfigCodes.FREQUENCY_LIMIT_IP_RATE);
                frequencyConfiguration.setLimitIpRate(Float.parseFloat(limitIpRate));
                frequencyConfiguration.getLimit().setItems(SystemConfig.getByCache(SystemConfigCodes.LIMIT_RULE_ITEMS));
                frequencyConfiguration.getLimit().setOutLimitLogTime(SystemConfig.getByCache(SystemConfigCodes.LIMIT_OUT_LIMIT_MIN_TIME));

                opendflConfiguration.getBaseLimit().setPageSizeMax(SystemConfig.getByCache(SystemConfigCodes.BASE_LIMIT_PAGE_SIZE_MAX));
                opendflConfiguration.getBaseLimit().setPageNumMax(SystemConfig.getByCache(SystemConfigCodes.BASE_LIMIT_PAGE_NUM_MAX));
                opendflConfiguration.getBaseLimit().setTotalRowMax(SystemConfig.getByCache(SystemConfigCodes.BASE_LIMIT_TOTAL_ROW_MAX));
                opendflConfiguration.getBaseLimit().setSearchDateMaxDay(SystemConfig.getByCache(SystemConfigCodes.BASE_LIMIT_SEARCH_DATE_DAY_MAX));
            } catch (Exception e) {
                log.warn("-----recoverFrequencyConfig--error={}", e.getMessage(), e);
            }
        }
    }

    /**
     * 次数限制读取系统参数
     *
     * @param frequency FrequencyVo
     */
    private FrequencyMysqlVo limitBySysconfig(FrequencyVo frequency) {
        String key = frequency.getName();
        DflFrequencyPo frequencyPo = dflFrequencyBiz.getFrequencyByCode(key, frequency.getFreqLimitType().getType(), frequency.getTime());
        if (frequencyPo != null) {
            return getFrequencyMysqlVo(frequency, frequencyPo);
        } else {
            //首次加载时自动保存
            autoCreateFrequency(frequency);
            return null;
        }
    }

    private FrequencyMysqlVo getFrequencyMysqlVo(FrequencyVo frequency, DflFrequencyPo frequencyPo) {
        FrequencyMysqlVo frequencyMysql = new FrequencyMysqlVo();
        frequencyMysql.setFrequency(frequency);
        frequencyMysql.setStatus(frequencyPo.getStatus());
        //如果数据状态无效，则用原来的默认值
        frequencyMysql.getFrequency().setLimit(frequencyPo.getLimitCount());
//        frequencyMysql.setFreqLimitType(FreqLimitType.parse(frequencyPo.getFreqLimitType()));
        frequencyMysql.getFrequency().setErrMsg(frequencyPo.getErrMsg());
        frequencyMysql.getFrequency().setErrMsgEn(frequencyPo.getErrMsgEn());
        frequencyMysql.setStatus(frequencyPo.getStatus());
        return frequencyMysql;
    }

    private void autoCreateFrequency(FrequencyVo frequency) {
        DflFrequencyPo entity = new DflFrequencyPo();
        entity.setCode(frequency.getName());
        entity.setFreqLimitType(frequency.getFreqLimitType().getType());
        entity.setLimitCount(frequency.getLimit());
        entity.setTime(frequency.getTime());
        entity.setAlias(frequency.getAliasName());
        entity.setAttrName(frequency.getAttrName());
        entity.setErrMsg(frequency.getErrMsg());
        entity.setErrMsgEn(frequency.getErrMsgEn());
        entity.setLimitType(frequency.getLimitType());
        entity.setLog(frequency.isLog() ? 1 : 0);
        entity.setNeedLogin(frequency.isNeedLogin() ? 1 : 0);
        entity.setWhiteCode(frequency.getWhiteCode());
        entity.setUri(frequency.getRequestUri());
        entity.setIfDel(0);
        entity.setStatus(1);
        this.dflFrequencyBiz.saveDflFrequency(entity);
    }

    private void autoCreateFrequencyByRequest(RequestVo requestVo, String requestUri) {
        DflFrequencyPo entity = new DflFrequencyPo();
        entity.setUri(requestUri);
        entity.setMethod(requestVo.getMethod());
        entity.setLimitType(FrequencyType.URI_CONFIG.getType());
        entity.setIfDel(0);
        entity.setStatus(1);
        entity.setNeedLogin(0);
        entity.setLog(0);
        this.dflFrequencyBiz.saveDflFrequency(entity);
    }

    /**
     * 次数限制读取系统参数
     *
     * @param requestVo FrequencyVo
     */
    @Override
    public void limitBySysconfigUri(RequestVo requestVo) {
        String requestUri = requestVo.getRequestUri();
        long curTime = System.currentTimeMillis();
        String key = "uri:" + requestVo.getRequestUri();
        String keyLoad = "uriLoad:" + requestVo.getRequestUri();
        Long time = loadSysconfigTimeMap.get(key);
        List<LimitUriConfigVo> list = uriLimitConfigsMap.get(requestUri);
        if (time == null || curTime - time > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            loadSysconfigTimeMap.put(key, curTime);
            this.recoverFrequencyConfig(curTime);
            Long maxUpdateTime = dflFrequencyBiz.getFrequencyByUriMaxUpdateTime(requestUri);
            if (maxUpdateTime == null) {
                maxUpdateTime = 0L;
            }
            Long loadTime = loadSysconfigTimeMap.get(keyLoad);
            //检查数据是否有变化，如果有变化才重新加载到内存
            if (loadTime == null || (loadTime.longValue() != maxUpdateTime.longValue())) {
                log.info("-----limitBySysconfigUri--uri={} maxUpdateTime={} reload config by modify_time changed loadTime={}", requestUri, maxUpdateTime, System.currentTimeMillis() - curTime);
                List<DflFrequencyPo> dflFrequencyPoList = dflFrequencyBiz.getFrequencyByUri(requestUri);
                list = cacheLimitUriConfigVos(requestVo, requestUri, keyLoad, maxUpdateTime, dflFrequencyPoList);
            }
        }
        if (list == null) {
            list = Collections.emptyList();
        }
        requestVo.setLimitRequests(list);
    }

    private List<LimitUriConfigVo> cacheLimitUriConfigVos(RequestVo requestVo, String requestUri, String keyLoad, Long maxUpdateTime, List<DflFrequencyPo> dflFrequencyPoList) {
        loadSysconfigTimeMap.put(keyLoad, maxUpdateTime);
        List<LimitUriConfigVo> list = null;
        if (CollectionUtils.isEmpty(dflFrequencyPoList)) {
            //首次加载时自动保存
//                autoCreateFrequencyByRequest(requestVo, requestUri);
            list = Collections.emptyList();
            uriLimitConfigsMap.put(requestUri, list);
        } else {
            list = getLimitUriConfigVos(dflFrequencyPoList, requestVo.getMethod(), requestUri);
            uriLimitConfigsMap.put(requestUri, list);
        }

        return list;
    }

    private List<LimitUriConfigVo> getLimitUriConfigVos(List<DflFrequencyPo> dflFrequencyPoList, String method, String requestUri) {
        List<LimitUriConfigVo> list = new ArrayList<>();
        for (DflFrequencyPo dflFrequencyPo : dflFrequencyPoList) {
            if (dflFrequencyPo.getTime() == null || dflFrequencyPo.getTime() == 0) {
                continue;
            }
            //uriConfig支持请求方法,如GET/POST
            if (CharSequenceUtil.isBlank(dflFrequencyPo.getMethod()) || dflFrequencyPo.getMethod().contains(method)) {
                LimitUriConfigVo uriConfigVo = DflFrequencyPo.toConfigVo(dflFrequencyPo);
                list.add(uriConfigVo);
            }
        }
        return list;
    }

    private static Long maxModifyTimeSysConfig = 0L;
    private static Long maxModifyTimeSysConfigLT = 0L;

    /**
     * 时支持根据修改时间，来重新加载系统参数配置
     *
     * @param curTime
     */
    private void loadAnyChange(Long curTime) {
        if (curTime - maxModifyTimeSysConfigLT > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            maxModifyTimeSysConfigLT = curTime;
            Long maxModifyTimeSysConfigData = dflFrequencyBiz.getFrequencyMaxUpdateTime();
            if (maxModifyTimeSysConfigData > maxModifyTimeSysConfig) {
                reloadNewlyModify(maxModifyTimeSysConfig, curTime);
                maxModifyTimeSysConfig = maxModifyTimeSysConfigData;
            }

            //批量修改缓存的加载时间
            loadSysconfigTimeMap.entrySet().stream().forEach(t -> {
                t.setValue(curTime);
            });
        }
    }

    /**
     * 重新加载有修改的数据
     */
    private void reloadNewlyModify(Long modifyTime, Long curTime) {
        List<DflFrequencyPo> modifys = dflFrequencyBiz.findFrequencyByNewlyModify(modifyTime);
        if (CollectionUtils.isEmpty(modifys)) {
            return;
        }
        List<DflFrequencyPo> modifyFrequencys = modifys.stream().filter(t -> t.getTime() != null && !FrequencyType.URI_CONFIG.getType().equals(t.getLimitType())).collect(Collectors.toList());
        Map<String, List<DflFrequencyPo>> modifyUriConfigs = modifys.stream().filter(t -> t.getTime() != null && FrequencyType.URI_CONFIG.getType().equals(t.getLimitType())).collect(Collectors.groupingBy(DflFrequencyPo::getUri));
        log.info("-------reloadNewlyModify---modifyTime={} modifys={} modifyFrequencys={} modifyUriConfigs={}", modifyTime, modifys.size(), modifyFrequencys.size(), modifyUriConfigs.size());
        reloadNewlyFrequency(modifyFrequencys, curTime);

        reloadNewlyUriConfigs(curTime, modifyUriConfigs);
    }

    private void reloadNewlyUriConfigs(Long curTime, Map<String, List<DflFrequencyPo>> modifyUriConfigs) {
        Set<Map.Entry<String, List<DflFrequencyPo>>> sets = modifyUriConfigs.entrySet();
        String keyLoad = null;
        String key = null;
        for (Map.Entry<String, List<DflFrequencyPo>> entry : sets) {
            String requestUri = entry.getKey();
            List<DflFrequencyPo> dflFrequencyPoList = entry.getValue();
            String method = dflFrequencyPoList.get(0).getMethod();

            //复用已有方法
            RequestVo requestVo = new RequestVo();
            requestVo.setRequestUri(requestUri);
            requestVo.setMethod(method);

            List<DflFrequencyPo> frequencyPoList = entry.getValue();
            Optional<DflFrequencyPo> maxOp = frequencyPoList.stream().max(Comparator.comparing(t -> t.getModifyTime().getTime()));
            Long maxUpdateTime = 0L;
            if (maxOp.isPresent()) {
                maxUpdateTime = maxOp.get().getModifyTime().getTime();
            }
            key = "uri:" + requestVo.getRequestUri();
            loadSysconfigTimeMap.put(key, curTime);
            keyLoad = "uriLoad:" + requestVo.getRequestUri();
            log.info("----reloadNewlyUriConfigs--cacheKey={} uri={}", keyLoad, requestVo.getRequestUri());
            cacheLimitUriConfigVos(requestVo, requestUri, keyLoad, maxUpdateTime, frequencyPoList);
        }
    }

    private void reloadNewlyFrequency(List<DflFrequencyPo> modifyFrequencys, Long curTime) {
        String cacheKey;
        for (DflFrequencyPo modifyInfo : modifyFrequencys) {
            if (CharSequenceUtil.isBlank(modifyInfo.getCode()) || modifyInfo.getTime() == null) {
                continue;
            }
            cacheKey = modifyInfo.getCode() + ":" + modifyInfo.getFreqLimitType() + ":" + modifyInfo.getTime();
            FrequencyMysqlVo frequencyMysqlExist = sysconfigLimitMap.get(cacheKey);
            if (frequencyMysqlExist == null) {
                log.info("----reloadNewlyFrequency--cacheKey={} uri={}", cacheKey, modifyInfo.getUri());
            }
            FrequencyVo frequencyVo = DflFrequencyPo.toFrequencyVo(modifyInfo);
            frequencyVo.setRequestUri(modifyInfo.getUri());
            frequencyVo.setNeedLogin(NumberUtil.equals(modifyInfo.getNeedLogin(), 1));
            frequencyVo.setLog(NumberUtil.equals(modifyInfo.getLog(), 1));
            frequencyVo.setWhiteCode(modifyInfo.getWhiteCode());
            frequencyVo.setCreateTime(modifyInfo.getModifyTime().getTime());
            loadSysconfigTimeMap.put(cacheKey, curTime);
            FrequencyMysqlVo frequencyMysqlNew = getFrequencyMysqlVo(frequencyVo, modifyInfo);
            sysconfigLimitMap.put(cacheKey, frequencyMysqlNew);
        }
    }
}
