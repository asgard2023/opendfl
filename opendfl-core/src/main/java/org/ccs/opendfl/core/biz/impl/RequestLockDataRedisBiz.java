package org.ccs.opendfl.core.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IRequestLockDataBiz;
import org.ccs.opendfl.core.limitlock.RequestLockHandlerInterceptor;
import org.ccs.opendfl.core.utils.locktools.LockUtils;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjh
 */
@Service(value = "requestLockDataRedisBiz")
@Slf4j
public class RequestLockDataRedisBiz implements IRequestLockDataBiz {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 查询用户的分布式锁的数据
     */
    @Override
    public List<RequestLockVo> requestLocks(String data) {
        Collection<RequestLockVo> limits = RequestLockHandlerInterceptor.locksMap.values();
        List<RequestLockVo> tmpList = new ArrayList<>();
        for (RequestLockVo lockVo : limits) {
            String redisKey = LockUtils.getLockRedisKey(lockVo.getName(), data);
            boolean isExist = redisTemplate.hasKey(redisKey);
            if (isExist) {
                long second = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
                RequestLockVo copy = lockVo.toCopy();
                copy.setErrMsg("lock:expireSec:" + second);
                tmpList.add(copy);
            }
        }
        return tmpList;
    }


    @Override
    public String lockEvict(String name, String data) {
        String redisKey = LockUtils.getLockRedisKey(name, data);
        long expireSec = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        log.info("-----lockEvict--redisKey={} expireSec={}", redisKey, expireSec);
        redisTemplate.delete(redisKey);
        return name + "=" + expireSec;
    }


}
