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
     * @return 每个编码对应的调用次数
     */
    Map<String, Integer> saveLimitCount();

    /**
     * 获取每个类型下的接口统计数量
     *
     * @param curTime 当前时间
     * @return 当日调用类型接口个数
     */
    Map<String, Integer> getTypeUriCount(Long curTime);

    List<ComboxItemVo> getRunCountTypeByDay(Integer day);

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     * @param type 黑白名单类型
     * @param count 取zset的前count个
     * @return 接口运行次数查询
     */
    List<RunCountVo> getNewlyRunCount(WhiteBlackCheckType type, Integer count);

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     * @param type 黑白名单类型
     * @param dateTime 当前时间戳
     * @param count 取zset的前count个
     *
     * @return 接口运行次数查询
     */
    List<RunCountVo> getNewlyRunCount(WhiteBlackCheckType type, Long dateTime, Integer count);
}
