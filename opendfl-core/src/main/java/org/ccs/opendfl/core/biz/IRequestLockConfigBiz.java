package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.vo.RequestLockVo;

/**
 * 分布式锁配置管理
 *
 * @author chenjh
 */
public interface IRequestLockConfigBiz {
    void loadLockConfig(RequestLockVo requestLockVo, Long curTime);
}
