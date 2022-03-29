package org.ccs.opendfl.core.limitcount;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.vo.LimitFrequencyConfigVo;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FrequencyConfigUtils {
    private static Logger logger = LoggerFactory.getLogger(FrequencyConfigUtils.class);

    private FrequencyConfigUtils() {

    }


    private static RedisTemplate<String, Object> redisTemplateJson;
    private static FrequencyConfiguration frequencyConfiguration;

    @Resource(name = "redisTemplateJson")
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplateJson) {
        FrequencyConfigUtils.redisTemplateJson = redisTemplateJson;
    }

    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FrequencyConfigUtils.frequencyConfiguration = frequencyConfiguration;
    }


    /**
     * 有修改日志一下
     *
     * @param frequency
     */
    private static boolean checkChange(FrequencyVo frequencyExist, FrequencyVo frequency) {
        boolean isChanged = false;
        if (frequencyExist == null || frequencyExist.hashCode() != frequency.hashCode()) {
            isChanged = true;
        }
        if (isChanged) {
            logger.info("----checkChange--name={} time={} limit={} ipUser={} userIp={}", frequency.getName(), frequency.getTime(), frequency.getLimit(), frequency.getIpUserCount(), frequency.getUserIpCount());
        }
        return isChanged;
    }

    private static Map<String, Long> loadSysconfigTimeMap = new ConcurrentHashMap<>();
    private static Map<String, FrequencyVo> sysconfigLimitMap = new ConcurrentHashMap<>();

    public static void limitBySysconfigLoad(FrequencyVo frequency, Long curTime) {
        if (!frequency.isSysconfig()) {
            return;
        }
        String key = frequency.getName() + ":" + frequency.getTime();
        Long time = loadSysconfigTimeMap.get(key);
        FrequencyVo frequencyExist = sysconfigLimitMap.get(key);
        if (time == null || curTime - time > 10000 || frequencyExist == null) {
            loadSysconfigTimeMap.put(key, curTime);
            FrequencyVo frequencyNew = limitBySysconfig(frequency);
            if (checkChange(frequencyExist, frequencyNew)) {
                //因为FrequencyVo是可以共用的，存起来也可以发生变化，所以这里缓存时clone一下
                sysconfigLimitMap.put(key, frequencyNew.clone());
            }
            frequencyExist = frequencyNew;
        }

        frequency.setLimit(frequencyExist.getLimit());
        frequency.setIpUserCount(frequencyExist.getIpUserCount());
        frequency.setUserIpCount(frequencyExist.getUserIpCount());
    }

    /**
     * 次数限制读取系统参数
     *
     * @param frequency
     */
    private static FrequencyVo limitBySysconfig(FrequencyVo frequency) {
        String aliasName = null;

        if (!"".equals(frequency.getAliasName())) {
            aliasName = frequency.getAliasName();
        }
        String key = (String) CommUtils.nvl(aliasName, frequency.getName());
        List<LimitFrequencyConfigVo> frequencyConfigList = frequencyConfiguration.getLimit().getFrequencyConfigs();
        LimitFrequencyConfigVo frequencyConfigVo = null;
        for (LimitFrequencyConfigVo configVo : frequencyConfigList) {
            if (StringUtils.equals(key, configVo.getName()) && frequency.getTime() == configVo.getTime()) {
                frequencyConfigVo = configVo;
                break;
            }
        }

        if (frequencyConfigVo != null) {
            frequency.setLimit(frequencyConfigVo.getLimit());
            frequency.setIpUserCount(frequencyConfigVo.getIpUser());
            frequency.setUserIpCount(frequencyConfigVo.getUserIp());
        }

        return frequency;
    }

    /**
     * 次数限制读取系统参数
     *
     * @param requestVo
     */
    public static void limitBySysconfig(RequestVo requestVo) {
        String key = requestVo.getRequestUri();
        List<LimitUriConfigVo> limitConfigs = frequencyConfiguration.getLimit().getUriConfigs();
        List<LimitUriConfigVo> list = new ArrayList<>();
        for (LimitUriConfigVo limitFrequencyConfigVo : limitConfigs) {
            if (StringUtils.equals(key, limitFrequencyConfigVo.getUri())) {
                list.add(limitFrequencyConfigVo);
            }
        }
        requestVo.setLimitRequests(list);
    }
}
