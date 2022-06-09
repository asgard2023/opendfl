package org.ccs.opendfl.core.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IOutLimitCountBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.utils.RedisTemplateUtil;
import org.ccs.opendfl.core.vo.ComboxItemVo;
import org.ccs.opendfl.core.vo.RunCountVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.ccs.opendfl.core.constants.FrequencyConstant.TIME_MILLISECOND_TO_DAY;

/**
 * 接口调用次数处理
 * 每个接口累计，忽略参数，只算接口调用次数
 *
 * @author chenjh
 */
@Service
@Slf4j
public class OutLimitCountRedisBiz implements IOutLimitCountBiz {
    private final RedisTemplate<String, String> redisTemplateString;
    private final FrequencyConfiguration frequencyConfiguration;

    public OutLimitCountRedisBiz(RedisTemplate<String, String> redisTemplateString, FrequencyConfiguration frequencyConfiguration) {
        this.redisTemplateString = redisTemplateString;
        this.frequencyConfiguration = frequencyConfiguration;
    }

    public String getRedisKeyCount(WhiteBlackCheckType type, Long curTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curTime);
        return frequencyConfiguration.getRedisPrefix() + ":" + type.getCode() + ":" + cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Map<String, Integer> getTypeUriCount(Long curTime) {
        Map<String, Integer> map = new HashMap<>();
        WhiteBlackCheckType[] types = WhiteBlackCheckType.values();
        for (WhiteBlackCheckType type : types) {
            int size = getRunTypeSize(type, curTime);
            map.put(type.getCode(), size);
        }
        return map;
    }

    private boolean existRun(Long curTime) {
        WhiteBlackCheckType[] types = WhiteBlackCheckType.values();
        for (WhiteBlackCheckType type : types) {
            int size = getRunTypeSize(type, curTime);
            if (size > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean existRun(WhiteBlackCheckType type, Long curTime) {
        int size = getRunTypeSize(type, curTime);
        return size > 0;
    }

    /**
     * 查类型对应的接口数
     *
     * @param type    WhiteBlackCheckType
     * @param curTime Long
     * @return Integer 当日调用接口个数
     */
    private Integer getRunTypeSize(WhiteBlackCheckType type, Long curTime) {
        String redisKey = getRedisKeyCount(type, curTime);
        Long size = redisTemplateString.opsForZSet().size(redisKey);
        if (size == null) {
            size = 0L;
        }
        return size.intValue();
    }

    @Override
    public List<ComboxItemVo> getRunCountTypeByDay(Integer day) {
        List<ComboxItemVo> list = new ArrayList<>();
        Long curTime = System.currentTimeMillis();
        Long curTimeBefore = curTime;
        if (day > 0) {
            curTimeBefore = curTime - day * TIME_MILLISECOND_TO_DAY;
        }
        WhiteBlackCheckType[] types = WhiteBlackCheckType.values();
        for (WhiteBlackCheckType type : types) {
            if (existRun(type, curTimeBefore)) {
                list.add(new ComboxItemVo(type.getCode(), "超限-" + type.getTypeName(), false));
            }
        }

        return list;
    }

    @Override
    public Map<String, Integer> saveLimitCount() {
        Long curTime = System.currentTimeMillis();
        WhiteBlackCheckType[] types = WhiteBlackCheckType.values();
        Map<String, Integer> typeCountMap = new HashMap<>();
        for (WhiteBlackCheckType type : types) {
            int count = saveLimitCountByType(type);
            typeCountMap.put(type.getCode(), count);
        }

        log.info("-----saveLimitCount--runTime={} typeCountMap={}", System.currentTimeMillis() - curTime, typeCountMap);
        return typeCountMap;
    }

    private int saveLimitCountByType(WhiteBlackCheckType type) {
        Integer cacheDay = frequencyConfiguration.getRunCountCacheDay();
        if (cacheDay == 0) {
            return 0;
        }
        long curTime = System.currentTimeMillis();
        Map<String, AtomicInteger> counterMap = FrequencyUtils.outLimitCountMap.get(type.getCode());
        if (counterMap == null) {
            return 0;
        }
        Set<Map.Entry<String, AtomicInteger>> sets = counterMap.entrySet();
        //通过key每天存一份
        String redisKeyCount = getRedisKeyCount(type, curTime);

        //缓存3天
        RedisTemplateUtil.expireTimeHashCacheString(redisTemplateString, redisKeyCount, cacheDay * 24);
        //前20次保存有输出日志，后面不再输出日志
        boolean isLog = FrequencyUtils.isInitLog("saveRunCount");
        int limitCount = 0;
        for (Map.Entry<String, AtomicInteger> entity : sets) {
            //保存接口调用次数
            limitCount += saveCount(redisKeyCount, entity.getKey(), entity.getValue(), isLog);
        }
        log.info("-----saveLimitCountByType--type={} limitCount={} runTime={}", type, limitCount, System.currentTimeMillis() - curTime);
        return limitCount;
    }

    private int saveCount(String redisKey, String requestUri, AtomicInteger counter, boolean isLog) {
        Integer count = counter.get();
        if (count > 0) {
            this.redisTemplateString.opsForZSet().add(redisKey, requestUri, count);
            //存保count到redis后，计算器扣减
            int v = counter.addAndGet(-count);
            if (isLog) {
                log.debug("----saveCount-redisKey={} uri={} count={} v={}", redisKey, requestUri, count, v);
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
    public List<RunCountVo> getNewlyRunCount(WhiteBlackCheckType type, Integer count) {
        Long curTime = System.currentTimeMillis();
        return getNewlyRunCount(type, curTime, count);
    }

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    @Override
    public List<RunCountVo> getNewlyRunCount(WhiteBlackCheckType type, Long dateTime, Integer count) {
        Long curTime = dateTime;
        String redisKeyCount = getRedisKeyCount(type, curTime);

        Set<ZSetOperations.TypedTuple<String>> setsRunTime = redisTemplateString.opsForZSet().reverseRangeWithScores(redisKeyCount, 0, count);
        List<RunCountVo> voList = new ArrayList<>();
        RunCountVo vo = null;
        for (ZSetOperations.TypedTuple<String> tupleTime : setsRunTime) {
            String uri = tupleTime.getValue();
            Double score = tupleTime.getScore();
            if (score == null) {
                score = 0D;
            }
            vo = new RunCountVo(uri, score.intValue());
            voList.add(vo);
        }
        return voList;
    }
}
