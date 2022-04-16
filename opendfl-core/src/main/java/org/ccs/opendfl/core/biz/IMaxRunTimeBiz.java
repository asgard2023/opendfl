package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.vo.MaxRunTimeVo;

import java.util.List;

/**
 * 记录接口执行的最大时间，一般用于超大时间发生改改时
 *
 * @author chenjh
 */
public interface IMaxRunTimeBiz {
    /**
     * 增加最大执行时行
     *
     * @param uri 接口uri(不含参数)
     * @param curTime 发生时间
     * @param maxRunTime 执行时长
     */
    void addMaxRunTime(String uri, Long curTime, Long maxRunTime);

    /**
     * 找出second时间内执行最慢的近count个接口
     * @param second 时间单位秒
     * @param count 接口个数
     * @return
     */
    List<MaxRunTimeVo> getNewlyMaxRunTime(Integer second, Integer count);

    List<MaxRunTimeVo> getNewlyMaxRunTime(Long dateTime, Integer second, Integer count);
}
