package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestVo;

/**
 * 频率限制配置
 *
 * @author chenjh
 */
public interface IFrequencyConfigBiz {
    /**
     * 基于frequency.name的配置
     * @param frequency 频率限制
     * @param curTime 当前时间
     */
    void limitBySysconfigLoad(FrequencyVo frequency, Long curTime);

    /**
     * 基于uri的配置
     * @param requestVo 频率限制
     */
    void limitBySysconfigUri(RequestVo requestVo);
}
