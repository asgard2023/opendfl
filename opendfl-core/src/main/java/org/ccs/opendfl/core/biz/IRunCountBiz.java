package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.constants.RunCountType;
import org.ccs.opendfl.core.vo.ComboxItemVo;
import org.ccs.opendfl.core.vo.MaxRunTimeVo;
import org.ccs.opendfl.core.vo.RunCountVo;

import java.util.List;
import java.util.Map;

/**
 * 接口调用次数处理
 * 每个接口累计，忽略参数，只算接口调用次数
 *
 * @author chenjh
 */
public interface IRunCountBiz {
    /**
     * 保存接口调用次数
     */
    void saveRunCount();

    /**
     * 获取每个类型下的接口统计数量
     * @param curTime
     * @return
     */
    public Map<String, Integer> getTypeUriCount(Long curTime);

    /**
     * 查出有运行记录的天，用于下拉框选择
     * @return
     */
    public List<ComboxItemVo> getRunDays();

    public List<ComboxItemVo> getRunCountTypeByDay(Integer day);

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    List<RunCountVo> getNewlyRunCount(RunCountType type, Integer count);

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    List<RunCountVo> getNewlyRunCount(RunCountType type, Long dateTime, Integer count);
}