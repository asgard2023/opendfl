package org.ccs.opendfl.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisTemplateUtil {
    private RedisTemplateUtil(){

    }
    private static final Logger logger = LoggerFactory.getLogger(RedisTemplateUtil.class);

    public static void expireTimeTTL(RedisTemplate<String, Object> redisTemplate, String redisKey, int seconds){
        Long v=redisTemplate.getExpire(redisKey);
        if(v==-1L) {
            redisTemplate.expire(redisKey, seconds, TimeUnit.SECONDS);
        }
    }
}
