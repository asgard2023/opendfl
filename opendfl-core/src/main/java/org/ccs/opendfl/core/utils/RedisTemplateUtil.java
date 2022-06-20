package org.ccs.opendfl.core.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
public class RedisTemplateUtil {
    private RedisTemplateUtil() {

    }

    private static final Logger logger = LoggerFactory.getLogger(RedisTemplateUtil.class);

    public static final Cache<String, Long> redisKeyMap = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS)
            .maximumSize(5000).build();

    /**
     * 不能用于含有大量redisKey的数据集处理，比如含userId，那样会造成缓存暴增
     *
     * @param redisKey String
     * @param hour 过期时间
     */
    public static void expireTimeHashCache(RedisTemplate<String, Object> redisTemplate, String redisKey, int hour) {
        if (redisKeyMap.getIfPresent(redisKey) == null) {
            redisKeyMap.put(redisKey, System.currentTimeMillis());
            redisTemplate.expire(redisKey, hour, TimeUnit.HOURS);
        }
    }

    public static void expireTimeHashCacheString(RedisTemplate<String, String> redisTemplate, String redisKey, int hour) {
        if (redisKeyMap.getIfPresent(redisKey) == null) {
            redisKeyMap.put(redisKey, System.currentTimeMillis());
            redisTemplate.expire(redisKey, hour, TimeUnit.HOURS);
        }
    }

    public static final Cache<String, Long> redisKeyHourMap = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(10000).build();

    /**
     * 频率限制1小时专用
     * 主要用于避免服务重启造成部份key变成永久key
     * @param redisKey
     */
    public static void expireTimeHashFrequencyCache(RedisTemplate<String, Object> redisTemplate, String redisKey, int time, long v){
        //time>60以及访问次数>=3检查一下是否永久key
        //time<=60秒的也忽略，就让用户多等一下
        if(time>60 && v<4) {
            if (redisKeyHourMap.getIfPresent(redisKey) == null) {
                redisKeyHourMap.put(redisKey, System.currentTimeMillis());
                expireTimeTTL(redisTemplate, redisKey, 3600);
            }
        }
    }


    public static void expireTimeTTL(RedisTemplate<String, Object> redisTemplate, String redisKey, int seconds) {
        Long v = redisTemplate.getExpire(redisKey);
        if (v == -1L) {
            redisTemplate.expire(redisKey, seconds, TimeUnit.SECONDS);
        }
    }
}
