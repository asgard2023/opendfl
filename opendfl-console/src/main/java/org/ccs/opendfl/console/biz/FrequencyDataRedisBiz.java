package org.ccs.opendfl.console.biz;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.limitfrequency.FrequencyHandlerInterceptor;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.limitlock.RequestLockHandlerInterceptor;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitIpUserStrategy;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitUserCountStrategy;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitUserIpStrategy;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chenjh
 */
@Service(value = "frequencyDataRedisBiz")
@Slf4j
public class FrequencyDataRedisBiz implements IFrequencyDataBiz {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询用户的限制数据
     *
     */
    @Override
    public List<FrequencyVo> limitUsers(String account) {
        Collection<FrequencyVo> limits = FrequencyHandlerInterceptor.freqMap.values();
        List<FrequencyVo> tmpList = new ArrayList<>();
        boolean isExist;
        String redisKey;
        for (FrequencyVo frequency : limits) {
            redisKey = FreqLimitUserCountStrategy.getRedisKey(frequency, account, null);
            isExist = redisTemplate.hasKey(redisKey);
            if (isExist) {
                FrequencyVo copy = frequency.toCopy();
                long count = redisTemplate.opsForValue().increment(redisKey, 0);
                Long expireSecond=redisTemplate.getExpire(redisKey);
                copy.setErrMsg("limit:count="+count+",expireSec="+expireSecond);
                tmpList.add(copy.toCopy());
            }
            else if(frequency.getUserIpCount()>0){
                redisKey = FreqLimitUserIpStrategy.getRedisKey(frequency, account);
                isExist = redisTemplate.hasKey(redisKey);
                if (isExist) {
                    FrequencyVo copy = frequency.toCopy();
                    long size = redisTemplate.opsForSet().size(redisKey);
                    Set values = redisTemplate.opsForSet().members(redisKey);
                    copy.setErrMsg("userIp:size=" + size + ",values=" + values);
                    tmpList.add(copy.toCopy());
                }
            }
        }
        return tmpList;
    }

    /**
     * 查询用户的限制数据
     *
     */
    @Override
    public List<FrequencyVo> limitIps(String ip) {
        if (!StringUtils.isNumeric(ip)) {
            ip = "" + RequestUtils.getIpConvertNum(ip);
        }
        Collection<FrequencyVo> limits = FrequencyHandlerInterceptor.freqMap.values();
        List<FrequencyVo> tmpList = new ArrayList<>();
        for (FrequencyVo frequency : limits) {
            if(frequency.getIpUserCount()==0){
                continue;
            }
            String redisKey = FreqLimitIpUserStrategy.getRedisKey(frequency, ip);
            boolean isExist = redisTemplate.hasKey(redisKey);
            if (isExist) {
                long size = redisTemplate.opsForSet().size(redisKey);
                Set values=redisTemplate.opsForSet().members(redisKey);
                Long expireSecond=redisTemplate.getExpire(redisKey);
                FrequencyVo copy = frequency.toCopy();
                copy.setErrMsg("ipUser:size="+size+",expireSec="+expireSecond+",values="+values);
                tmpList.add(copy.toCopy());
            }
        }
        return tmpList;
    }

    /**
     * 查询用户的分布式锁的数据
     *
     */
    @Override
    public List<RequestLockVo> requestLocks(String data) {
        Collection<RequestLockVo> limits = RequestLockHandlerInterceptor.locksMap.values();
        List<RequestLockVo> tmpList = new ArrayList<>();
        for (RequestLockVo lockVo : limits) {
            String redisKey = FrequencyUtils.getRedisKeyLock(lockVo.getName(), data);
            boolean isExist = redisTemplate.hasKey(redisKey);
            if (isExist) {
                long second = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
                RequestLockVo copy=lockVo.toCopy();
                copy.setErrMsg("lock:expireSec:"+second);
                tmpList.add(copy);
            }
        }
        return tmpList;
    }


    @Override
    public String lockEvict(String name, String data){
        String redisKey = FrequencyUtils.getRedisKeyLock(name, data);
        long expireSec=redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        log.info("-----lockEvict--redisKey={} expireSec={}", redisKey, expireSec);
        redisTemplate.delete(redisKey);
        return name+"="+expireSec;
    }

    @Override
    public List<String> freqEvictList(String code, List<Integer> timeList, String account) {
        List<String> list = new ArrayList<>();
        FrequencyVo frequencyVo = new FrequencyVo();
        String info;
        for (Integer time : timeList) {
            frequencyVo.setName(code);
            frequencyVo.setTime(time);
            info = freqEvict(frequencyVo, account);
            list.add(info);
            info = freqUserIpEvict(frequencyVo, account);
            list.add(info);
        }
        return list.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    /**
     * 重置用户频率限制次数
     *
     */
    @Override
    public String freqEvict(FrequencyVo frequency, String account) {
        String key = FreqLimitUserCountStrategy.getRedisKey(frequency, account, null);
        boolean isExist = redisTemplate.hasKey(key);
        if (isExist) {
            Long count = redisTemplate.opsForValue().increment(key, 0);
            log.info("----freqEvict--redisKey={} count={}", key, count);
            redisTemplate.delete(key);
            return key + "=" + count;
        }
        return null;
    }

    /**
     * 重置同一用户多个IP登入限制
     *
     */
    @Override
    public String freqIpUserEvict(FrequencyVo frequency, String account) {
        String redisKey = FreqLimitIpUserStrategy.getRedisKey(frequency, account);
        boolean isExist = redisTemplate.hasKey(redisKey);
        if (!isExist) {
            return null;
        }
        long count = redisTemplate.opsForSet().size(redisKey);
        log.info("----freqIpUserEvict--redisKey={} count={}", redisKey, count);
        redisTemplate.delete(redisKey);
        return redisKey + "=" + count;
    }

    /**
     * 重置同一用户多个IP登入限制
     *
     */
    @Override
    public String freqUserIpEvict(FrequencyVo frequency, String account) {
        String redisKey = FreqLimitUserIpStrategy.getRedisKey(frequency, account);
        boolean isExist = redisTemplate.hasKey(redisKey);
        if (!isExist) {
            return null;
        }
        long count = redisTemplate.opsForSet().size(redisKey);
        log.info("----freqUserIpEvict--redisKey={} count={}", redisKey, count);
        redisTemplate.delete(redisKey);
        return redisKey + "=" + count;
    }

}
