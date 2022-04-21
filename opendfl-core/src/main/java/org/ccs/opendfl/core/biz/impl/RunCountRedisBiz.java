package org.ccs.opendfl.core.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IMaxRunTimeBiz;
import org.ccs.opendfl.core.biz.IRunCountBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.RunCountType;
import org.ccs.opendfl.core.limitfrequency.FrequencyHandlerInterceptor;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.utils.RedisTemplateUtil;
import org.ccs.opendfl.core.vo.ComboxItemVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.ccs.opendfl.core.vo.RunCountVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.ccs.opendfl.core.constants.FrequencyConstant.TIME_MILLISECOND_TO_DAY;

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
    private final IMaxRunTimeBiz maxRunTimeBiz;

    public RunCountRedisBiz(RedisTemplate<String, String> redisTemplateString, FrequencyConfiguration frequencyConfiguration, IMaxRunTimeBiz maxRunTimeBiz) {
        this.redisTemplateString = redisTemplateString;
        this.frequencyConfiguration = frequencyConfiguration;
        this.maxRunTimeBiz = maxRunTimeBiz;
    }

    public String getRedisKeyCount(RunCountType type, Long curTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curTime);
        return frequencyConfiguration.getRedisPrefix() + ":" + type.getCode() + ":" + cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Map<String, Integer> getTypeUriCount(Long curTime) {
        Map<String, Integer> map = new HashMap<>();
        RunCountType[] types = RunCountType.values();
        for (RunCountType type : types) {
            int size = getRunTypeSize(type, curTime);
            map.put(type.getCode(), size);
        }
        return map;
    }

    /**
     * 查类型对应的接口数
     *
     * @param type    RunCountType
     * @param curTime Long
     * @return Integer 当日调用接口个数
     */
    private Integer getRunTypeSize(RunCountType type, Long curTime) {
        String redisKey = getRedisKeyCount(type, curTime);
        Long size = redisTemplateString.opsForZSet().size(redisKey);
        if (size == null) {
            size = 0L;
        }
        return size.intValue();
    }

    private boolean existRun(Long curTime) {
        RunCountType[] types = RunCountType.values();
        for (RunCountType type : types) {
            int size = getRunTypeSize(type, curTime);
            if (size > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean existRun(RunCountType type, Long curTime) {
        int size = getRunTypeSize(type, curTime);
        return size > 0;
    }

    @Override
    public List<ComboxItemVo> getRunCountTypeByDay(Integer day) {
        List<ComboxItemVo> list = new ArrayList<>();
        Long curTime = System.currentTimeMillis();
        Long curTimeBefore = curTime;
        if (day > 0) {
            curTimeBefore = curTime - day * TIME_MILLISECOND_TO_DAY;
        }
        RunCountType[] types = RunCountType.values();
        for (RunCountType type : types) {
            if (existRun(type, curTimeBefore)) {
                list.add(new ComboxItemVo(type.getCode(), type.getTypeName(), false));
            }
        }
        return list;
    }

    @Override
    public List<ComboxItemVo> getRunDays() {
        Long curTime = System.currentTimeMillis();
        List<ComboxItemVo> list = new ArrayList<>();

        list.add(new ComboxItemVo("-1", "当前(近30s)", true));
        if (frequencyConfiguration.getRunCountCacheDay() == 0) {
            list.add(new ComboxItemVo("-1", "frequency.runCountCacheDay=0 Closed", false));
            return list;
        }
        for (int i = 0; i < frequencyConfiguration.getRunCountCacheDay(); i++) {
            Long curTimeBefore = curTime - i * TIME_MILLISECOND_TO_DAY;
            if (existRun(curTimeBefore)) {
                String name = i + "天前";
                if (i == 0) {
                    name = "今天";
                }
                list.add(new ComboxItemVo("" + i, name, false));
            }
        }
        return list;
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
        String redisKeyCount = getRedisKeyCount(RunCountType.COUNT, curTime);
        String redisKeyLimitCount = getRedisKeyCount(RunCountType.OUT_LIMIT, curTime);
        Integer cacheDay = frequencyConfiguration.getRunCountCacheDay();
        //缓存3天
        RedisTemplateUtil.expireTimeHashCacheString(redisTemplateString, redisKeyCount, cacheDay * 24);
        RedisTemplateUtil.expireTimeHashCacheString(redisTemplateString, redisKeyLimitCount, cacheDay * 24);
        //前20次保存有输出日志，后面不再输出日志
        boolean isLog = FrequencyUtils.isInitLog("saveRunCount");
        int reqCount = 0;
        int limitCount = 0;
        for (Map.Entry<String, RequestVo> entity : sets) {
            RequestVo requestVo = entity.getValue();
            //保存接口调用次数
            reqCount += saveRunCount(redisKeyCount, requestVo.getRequestUri(), requestVo.getCounter(), isLog);
            //保存接口调用限制次数
            limitCount += saveRunCount(redisKeyLimitCount, requestVo.getRequestUri(), requestVo.getLimitCounter(), isLog);
            if(requestVo.getMaxRunTime()>0) {
                //保存成功后，重置maxRun信息
                this.maxRunTimeBiz.addMaxRunTime(requestVo.getRequestUri(), requestVo.getMaxRunTimeCreateTime(), requestVo.getMaxRunTime());
                requestVo.setMaxRunTime(0L);
                requestVo.setMaxRunTimeCreateTime(0L);
            }
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
    public List<RunCountVo> getNewlyRunCount(RunCountType type, Integer count) {
        Long curTime = System.currentTimeMillis();
        return getNewlyRunCount(type, curTime, count);
    }

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    @Override
    public List<RunCountVo> getNewlyRunCount(RunCountType type, Long curTime, Integer count) {
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
            Integer runCount = score.intValue();
            vo = new RunCountVo(uri, runCount);
            voList.add(vo);
        }
        return voList;
    }
}
