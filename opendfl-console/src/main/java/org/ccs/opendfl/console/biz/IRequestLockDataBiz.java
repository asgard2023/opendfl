package org.ccs.opendfl.console.biz;

import org.ccs.opendfl.core.vo.RequestLockVo;

import java.util.List;

/**
 * @author chenjh
 */
public interface IRequestLockDataBiz {


    /**
     * 查询用户的分布式锁的数据
     *
     * @param data attrName(默认userId)的值
     * @return List<RequestLockVo>
     */
    List<RequestLockVo> requestLocks(String data);

    /**
     * 删除锁数据
     *
     * @param name @RequestLock.name
     * @param data attrName(默认userId)的值
     * @return 删除的Key及数量
     */
    String lockEvict(String name, String data);

}
