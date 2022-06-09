package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.vo.ComboxItemVo;
import org.ccs.opendfl.core.vo.RunCountVo;

import java.util.List;
import java.util.Map;

/**
 * 接口调用次数处理
 *
 * @author chenjh
 */
public interface IOutLimitCountBiz {
    /**
     * 保存接口调用次数
     */
    Map<String, Integer> saveLimitCount();

    /**
     * 获取每个类型下的接口统计数量
     *
     * @param curTime
     * @return
     */
    public Map<String, Integer> getTypeUriCount(Long curTime);

    public List<ComboxItemVo> getRunCountTypeByDay(Integer day);

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    List<RunCountVo> getNewlyRunCount(WhiteBlackCheckType type, Integer count);

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    List<RunCountVo> getNewlyRunCount(WhiteBlackCheckType type, Long dateTime, Integer count);
}
