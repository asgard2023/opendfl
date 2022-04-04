package org.ccs.opendfl.core.limitfrequency;


import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitIpUserStrategy;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitUserCountStrategy;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitUserIpStrategy;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FrequencyEvictUtil {
    static final Logger logger = LoggerFactory.getLogger(FrequencyEvictUtil.class);

    private FrequencyEvictUtil() {

    }

    public static List<String> freqEvictList(String code, List<Integer> timeList, String account, RedisTemplate<String, Object> redisTemplate) {
        List<String> list = new ArrayList<>();
        FrequencyVo frequencyVo = new FrequencyVo();
        String info = null;
        for (Integer time : timeList) {
            frequencyVo.setName(code);
            frequencyVo.setTime(time);
            info = freqEvict(frequencyVo, account, redisTemplate);
            list.add(info);
            info = freqUserIpEvict(frequencyVo, account, redisTemplate);
            list.add(info);
        }
        return list.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    /**
     * 重置用户频率限制次数
     *
     * @param frequency 对应的频限制
     * @param account userId或account等
     */
    public static String freqEvict(FrequencyVo frequency, String account, RedisTemplate<String, Object> redisTemplate) {
        String key = FreqLimitUserCountStrategy.getRedisKey(frequency, account, null);
        boolean isExist = redisTemplate.hasKey(key);
        if (isExist) {
            Long count = redisTemplate.opsForValue().increment(key, 0);
            redisTemplate.delete(key);
            logger.info("----freqEvict--key={} count={}", key, count);
            return key + "=" + count;
        }
        return null;
    }

    /**
     * 重置同一用户多个IP登入限制
     *
     * @param frequency 对应的频限制
     * @param account userId或account等
     * @return 删除的Key及数量
     */
    public static String freqIpUserEvict(FrequencyVo frequency, String account, RedisTemplate<String, Object> redisTemplate) {
        String redisKey = FreqLimitIpUserStrategy.getRedisKey(frequency, account);
        boolean isExist = redisTemplate.hasKey(redisKey);
        if (!isExist) {
            return null;
        }
        long count = redisTemplate.opsForSet().size(redisKey);
        redisTemplate.delete(redisKey);
        return redisKey + "=" + count;
    }

    /**
     * 重置同一用户多个IP登入限制
     *
     * @param frequency 对应的频限制
     * @param account userId或account等
     * @return 删除的Key及数量
     */
    public static String freqUserIpEvict(FrequencyVo frequency, String account, RedisTemplate<String, Object> redisTemplate) {
        String redisKey = FreqLimitUserIpStrategy.getRedisKey(frequency, account);
        boolean isExist = redisTemplate.hasKey(redisKey);
        if (!isExist) {
            return null;
        }
        long count = redisTemplate.opsForSet().size(redisKey);
        redisTemplate.delete(redisKey);
        return redisKey + "=" + count;
    }
}
