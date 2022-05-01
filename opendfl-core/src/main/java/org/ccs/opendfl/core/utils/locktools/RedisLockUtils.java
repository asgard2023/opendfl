package org.ccs.opendfl.core.utils.locktools;

import org.ccs.opendfl.core.limitlock.RequestLock;
import org.ccs.opendfl.core.utils.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis lock
 * @author chenjh
 */
@Component
public class RedisLockUtils {

    private static RedisTemplate<String, String> redisTemplateString;
    @Resource(name = "redisTemplateString")
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate){
        RedisLockUtils.redisTemplateString = redisTemplate;
    }

    public static boolean lock(RequestLock reqLimit, String lockKey, String rndId){
        return redisTemplateString.opsForValue().setIfAbsent(lockKey, rndId, reqLimit.time(), TimeUnit.SECONDS);
    }

    public static void unlock(String lockKey, String rndId){
        String v = redisTemplateString.opsForValue().get(lockKey);
        if (StringUtils.equals(rndId, v)) {
            redisTemplateString.delete(lockKey);
        }
    }
}
