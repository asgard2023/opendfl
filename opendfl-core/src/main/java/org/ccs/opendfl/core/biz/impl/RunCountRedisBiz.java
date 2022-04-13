package org.ccs.opendfl.core.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IRunCountBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.limitfrequency.FrequencyHandlerInterceptor;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.utils.RedisTemplateUtil;
import org.ccs.opendfl.core.vo.MaxRunTimeVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 接口调用次数处理
 *
 * @author chenjh
 */
@Service
@Slf4j
public class RunCountRedisBiz implements IRunCountBiz {
    private final RedisTemplate<String, String> redisTemplateString;
    private final FrequencyConfiguration frequencyConfiguration;

    public RunCountRedisBiz(RedisTemplate<String, String> redisTemplateString, FrequencyConfiguration frequencyConfiguration) {
        this.redisTemplateString = redisTemplateString;
        this.frequencyConfiguration = frequencyConfiguration;
    }

    public String getRedisKeyCount(Long curTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curTime);
        return frequencyConfiguration.getRedisPrefix() + ":runCount:" + cal.get(Calendar.DAY_OF_WEEK);
    }

    public String getRedisKeyLimitCount(Long curTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curTime);
        return frequencyConfiguration.getRedisPrefix() + ":limitCount:" + cal.get(Calendar.DAY_OF_WEEK);
    }


    /**
     * 保存接口调用次数
     */
    @Override
    public void saveRunCount() {
        long curTime = System.currentTimeMillis();
        Map<String, RequestVo> map = FrequencyHandlerInterceptor.requestVoMap;
        Set<Map.Entry<String, RequestVo>> sets = map.entrySet();
        //通过key每天存一份
        String redisKeyCount = getRedisKeyCount(curTime);
        String redisKeyLimitCount = getRedisKeyLimitCount(curTime);
        //缓存3天
        RedisTemplateUtil.expireTimeHashCacheString(redisTemplateString, redisKeyCount, 72);
        RedisTemplateUtil.expireTimeHashCacheString(redisTemplateString, redisKeyLimitCount, 72);
        //前20次保存有输出日志，后面不再输出日志
        boolean isLog = FrequencyUtils.isInitLog("savveRunCount");
        int reqCount = 0;
        int limitCount = 0;
        for (Map.Entry<String, RequestVo> entity : sets) {
            RequestVo requestVo = entity.getValue();
            //保存接口调用次数
            reqCount += saveRunCount(redisKeyCount, requestVo.getRequestUri(), requestVo.getCounter(), isLog);
            //保存接口调用限制次数
            limitCount += saveRunCount(redisKeyLimitCount, requestVo.getRequestUri(), requestVo.getLimitCounter(), isLog);
        }
        log.info("-----saveRunCount--reqCount={} limitCount={} runTime={}", reqCount, limitCount, System.currentTimeMillis() - curTime);
    }

    private int saveRunCount(String redisKey, String requestUri, AtomicInteger counter, boolean isLog) {
        Integer count = counter.get();
        if (count > 0) {
            this.redisTemplateString.opsForZSet().add(redisKey, requestUri, count);
            //存保count到redis后，计算器扣减
            int v = counter.addAndGet(-count);
            if (isLog) {
                log.debug("----saveRunCount-redisKey={} uri={} count={} v={}", redisKey, requestUri, count, v);
            }
            return 1;
        }
        return 0;
    }

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    @Override
    public List<MaxRunTimeVo> getNewlyRunCount(String type, Integer count) {
        Long curTime = System.currentTimeMillis();
        return getNewlyRunCount(type, curTime, count);
    }

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    @Override
    public List<MaxRunTimeVo> getNewlyRunCount(String type, Long dateTime, Integer count) {
        Long curTime = dateTime;
        String redisKeyCount = getRedisKeyCount(curTime);
        if ("limitCount".equals(type)) {
            redisKeyCount = getRedisKeyLimitCount(curTime);
        }

        Set<ZSetOperations.TypedTuple<String>> setsRunTime = redisTemplateString.opsForZSet().reverseRangeWithScores(redisKeyCount, 0, count);
        List<MaxRunTimeVo> voList = new ArrayList<>();
        MaxRunTimeVo vo = null;
        for (ZSetOperations.TypedTuple<String> tupleTime : setsRunTime) {
            String uri = tupleTime.getValue();
            Long time = tupleTime.getScore().longValue();
            vo = new MaxRunTimeVo(uri, null, time);
            voList.add(vo);
        }
        return voList;
    }
}
