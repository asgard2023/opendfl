package org.ccs.opendfl.core.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IFrequencyConfigBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.vo.LimitFrequencyConfigVo;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 频率限制配置查询-redis
 *
 * @author chenjh
 */
@Service(value = "frequencyConfigBiz")
@Slf4j
public class FrequencyConfigBiz implements IFrequencyConfigBiz {
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    /**
     * 有修改日志一下
     *
     * @param frequency FrequencyVo
     */
    private boolean checkChange(FrequencyVo frequencyExist, FrequencyVo frequency) {
        boolean isChanged = false;
        if (frequencyExist == null || frequencyExist.hashCode() != frequency.hashCode()) {
            isChanged = true;
        }
        if (isChanged) {
            log.info("----checkChange--name={} time={} limit={} freqLimitType={}", frequency.getName(), frequency.getTime(), frequency.getLimit(), frequency.getFreqLimitType());
        }
        return isChanged;
    }

    /**
     * 主要按接口缓存，理论上接口数不会太多，用Map做持久缓存，不会占太多内存
     */
    private static final Map<String, Long> loadSysconfigTimeMap = new ConcurrentHashMap<>(64);
    private static final Map<String, FrequencyVo> sysconfigLimitMap = new ConcurrentHashMap<>(64);

    @Override
    public void limitBySysconfigLoad(FrequencyVo frequency, Long curTime) {
        String key = frequency.getName()+":"+frequency.getFreqLimitType().getType() + ":" + frequency.getTime();
        Long time = loadSysconfigTimeMap.get(key);
        FrequencyVo frequencyExist = sysconfigLimitMap.get(key);
        if (time == null || curTime - time > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            loadSysconfigTimeMap.put(key, curTime);
            FrequencyVo frequencyNew = limitBySysconfig(frequency);
            if (frequencyNew != null && checkChange(frequencyExist, frequencyNew)) {
                //因为FrequencyVo是可以共用的，存起来也可以发生变化，所以这里缓存时clone一下
                sysconfigLimitMap.put(key, frequencyNew.toCopy());
            }
            frequencyExist = frequencyNew;
        }

        if (frequencyExist != null) {
            frequency.setLimit(frequencyExist.getLimit());
            frequency.setFreqLimitType(frequencyExist.getFreqLimitType());
            frequency.setSysconfig(frequencyExist.isSysconfig());
        }
    }

    /**
     * 次数限制读取系统参数
     *
     * @param frequency FrequencyVo
     */
    private FrequencyVo limitBySysconfig(FrequencyVo frequency) {
        String key = frequency.getName();
        List<LimitFrequencyConfigVo> frequencyConfigList = frequencyConfiguration.getLimit().getFrequencyConfigs();
        LimitFrequencyConfigVo frequencyConfigVo = null;
        for (LimitFrequencyConfigVo configVo : frequencyConfigList) {
            if (StringUtils.equals(key, configVo.getName()) && frequency.getTime() == configVo.getTime()
                    && configVo.getFreqLimitType()==frequency.getFreqLimitType()) {
                frequencyConfigVo = configVo;
                frequency.setSysconfig(true);
                break;
            }
        }

        if (frequencyConfigVo != null) {
            frequency.setLimit(frequencyConfigVo.getLimit());
            return frequency;
        }

        return null;
    }

    /**
     * 次数限制读取系统参数
     *
     * @param requestVo FrequencyVo
     */
    @Override
    public void limitBySysconfigUri(RequestVo requestVo) {
        String requestUri = requestVo.getRequestUri();
        List<LimitUriConfigVo> limitConfigs = frequencyConfiguration.getLimit().getUriConfigs();
        List<LimitUriConfigVo> list = new ArrayList<>();
        for (LimitUriConfigVo uriConfigVo : limitConfigs) {
            if (StringUtils.equals(requestUri, uriConfigVo.getUri())) {
                boolean isSameMethod = StringUtils.equals(uriConfigVo.getMethod(), requestVo.getMethod());
                boolean isEmptyMethod = StringUtils.isBlank(uriConfigVo.getMethod());
                //uriConfig支持请求方法,如GET/POST
                if (isEmptyMethod || !isEmptyMethod && isSameMethod) {
                    list.add(uriConfigVo);
                }
            }
        }
        requestVo.setLimitRequests(list);
    }
}
