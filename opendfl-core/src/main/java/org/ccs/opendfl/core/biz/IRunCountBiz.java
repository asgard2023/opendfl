package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.vo.MaxRunTimeVo;

import java.util.List;

/**
 * 接口调用次数处理
 *
 * @author chenjh
 */
public interface IRunCountBiz {
    /**
     * 保存接口调用次数
     */
    void saveRunCount();

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    List<MaxRunTimeVo> getNewlyRunCount(String type, Integer count);

    /**
     * 找出当前second时间内执行次数最多的前count个接口
     *
     * @return list
     */
    List<MaxRunTimeVo> getNewlyRunCount(String type, Long dateTime, Integer count);
}
