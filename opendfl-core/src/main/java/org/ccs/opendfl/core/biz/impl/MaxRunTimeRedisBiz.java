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
    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplateString;
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    private static final String REDIS_MAX_RUN_TIME = "maxRunTime";

    public String getRedisKey(Long curTime, String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curTime);
        return frequencyConfiguration.getRedisPrefix() + ":" + REDIS_MAX_RUN_TIME + ":" + type + ":" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 接口慢执行记录到redis
     *
     * @param uri        接口uri(不含参数)
     * @param maxRunTimeCreateTime    发生时间
     * @param maxRunTime 执行时长
     */
    @Override
    public void addMaxRunTime(String uri, Long maxRunTimeCreateTime, Long maxRunTime) {
        String redisKeyMaxTime = getRedisKey(maxRunTimeCreateTime, "time");
        redisTemplateString.opsForZSet().add(redisKeyMaxTime, uri, maxRunTimeCreateTime);
        RedisTemplateUtil.expireTimeHashCacheString(redisTemplateString, redisKeyMaxTime, 72);

        String redisKeyMaxRunTime = getRedisKey(maxRunTimeCreateTime, "run");
        redisTemplateString.opsForZSet().add(redisKeyMaxRunTime, uri, maxRunTime);
        RedisTemplateUtil.expireTimeHashCacheString(redisTemplateString, redisKeyMaxRunTime, 72);
    }

    /**
     * 找出当前second时间内执行最慢的近count个接口
     *
     * @param second 秒
     * @return
     */
    @Override
    public List<MaxRunTimeVo> getNewlyMaxRunTime(Integer second, Integer count) {
        Long curTime = System.currentTimeMillis();
        return getNewlyMaxRunTime(curTime, second, count);
    }

    /**
     * 找出second时间内执行最慢的近count个接口
     *
     * @param second 秒
     * @return
     */
    @Override
    public List<MaxRunTimeVo> getNewlyMaxRunTime(Long dateTime, Integer second, Integer count) {
        Long curTime = dateTime;
        String redisKeyMaxTime = getRedisKey(curTime, "time");
     log.debug("------getNewlyMaxRunTime--redisKeyMaxTime={}", redisKeyMaxTime);
        Set<ZSetOperations.TypedTuple<String>> setsTime = redisTemplateString.opsForZSet().reverseRangeByScoreWithScores(redisKeyMaxTime, curTime - second * 1000, curTime);
        String redisKeyMaxRunTime = getRedisKey(curTime, "run");
        Set<ZSetOperations.TypedTuple<String>> setsRunTime = redisTemplateString.opsForZSet().reverseRangeWithScores(redisKeyMaxRunTime, 0, count);
        List<MaxRunTimeVo> voList = new ArrayList<>();
        MaxRunTimeVo vo = null;
        for (ZSetOperations.TypedTuple<String> tupleTime : setsTime) {
            String uri =tupleTime.getValue();
            Long time = tupleTime.getScore().longValue();
            for (ZSetOperations.TypedTuple<String> tupleRunTime : setsRunTime) {
                String uriRun = tupleRunTime.getValue();
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
