package org.ccs.opendfl.core.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IMaxRunTimeBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.utils.RedisTemplateUtil;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.MaxRunTimeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * 记录接口执行的最大时间，一般用于超大时间发生改改时
 *
 * @author chenjh
 */
@Service(value = "maxRunTimeRedisBiz")
@Slf4j
public class MaxRunTimeRedisBiz implements IMaxRunTimeBiz {
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    private static final String REDIS_MAX_RUN_TIME = "maxRunTime";

    public String getRedisKey(Long curTime, String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curTime);
        return frequencyConfiguration.getRedisPrefix() + ":" + REDIS_MAX_RUN_TIME + ":" + type + ":" + calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 接口慢执行记录到redis
     * @param uri 接口uri(不含参数)
     * @param curTime 发生时间
     * @param maxRunTime 执行时长
     */
    @Override
    public void addMaxRunTime(String uri, Long curTime, Long maxRunTime) {
        String redisKeyMaxTime = getRedisKey(curTime, "time");
        redisTemplate.opsForZSet().add(redisKeyMaxTime, uri, curTime);
        RedisTemplateUtil.expireTimeHashCache(redisTemplate, redisKeyMaxTime, 12);

        String redisKeyMaxRunTime = getRedisKey(curTime, "run");
        redisTemplate.opsForZSet().add(redisKeyMaxRunTime, uri, maxRunTime);
        RedisTemplateUtil.expireTimeHashCache(redisTemplate, redisKeyMaxRunTime, 12);
    }

    /**
     * 找出second时间内执行最慢的近count个接口
     * @param second 秒
     * @return
     */
    @Override
    public List<MaxRunTimeVo> getNewlyMaxRunTime(Integer second, Integer count) {
        Long curTime = System.currentTimeMillis();
        String redisKeyMaxTime = getRedisKey(curTime, "time");
        Set<ZSetOperations.TypedTuple<Object>> setsTime = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(redisKeyMaxTime, curTime - second * 1000, curTime);
        String redisKeyMaxRunTime = getRedisKey(curTime, "run");
        Set<ZSetOperations.TypedTuple<Object>> setsRunTime = redisTemplate.opsForZSet().reverseRangeWithScores(redisKeyMaxRunTime, 0, count);
        List<MaxRunTimeVo> voList = new ArrayList<>();
        MaxRunTimeVo vo = null;
        for (ZSetOperations.TypedTuple<Object> tupleTime : setsTime) {
            String uri = (String) tupleTime.getValue();
            Long time = tupleTime.getScore().longValue();
            for (ZSetOperations.TypedTuple<Object> tupleRunTime : setsRunTime) {
                String uriRun = (String) tupleRunTime.getValue();
                Long timeRun = tupleRunTime.getScore().longValue();
                if (StringUtils.equals(uri, uriRun)) {
                    vo = new MaxRunTimeVo(uri, time, timeRun);
                    voList.add(vo);
                }
            }
        }
        return voList;
    }

}
