package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.vo.RequestLockVo;

import java.util.List;

/**
 * 分面式锁数据查询与管理
 * @author chenjh
 */
public interface IRequestLockDataBiz {


    /**
     * 查询用户的分布式锁的数据
     *
     * @param data attrName(默认userId)的值
     * @return 分布锁
     */
    List<RequestLockVo> requestLocks(String data);

    /**
     * 删除锁数据
     *
     * @param name 分布锁的功能名，一般是编码
     * @param data attrName(默认userId)的值
     * @return 删除的Key及数量
     */
    String lockEvict(String name, String data);

}
