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
     * @param frequency
     * @param curTime
     */
    void limitBySysconfigLoad(FrequencyVo frequency, Long curTime);

    /**
     * 基于uri的配置
     * @param requestVo
     */
    void limitBySysconfigUri(RequestVo requestVo);
}
