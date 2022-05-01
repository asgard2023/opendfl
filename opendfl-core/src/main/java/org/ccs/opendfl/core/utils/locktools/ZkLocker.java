package org.ccs.opendfl.core.utils.locktools;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * zk lock
 *
 * @author chenjh
 */
@Component
@Slf4j
public class ZkLocker {
    public ZkLocker(){

    }
    private static CuratorFramework curatorFramework;
    private InterProcessSemaphoreMutex lock;
    private RequestLock reqLimit;
    private String lockKey;

    @Autowired(required = false)
    public void setCuratorFramework(CuratorFramework curatorFramework) {
        ZkLocker.curatorFramework = curatorFramework;
    }

    public ZkLocker(RequestLock reqLimit, String lockKey) {
        this.reqLimit = reqLimit;
        this.lockKey = lockKey;
        lock = new InterProcessSemaphoreMutex(curatorFramework, lockKey);
    }

    public boolean lock() throws Exception {
        return lock.acquire(reqLimit.time(), TimeUnit.SECONDS);
    }

    public void unlock() throws Exception {
        if (lock != null) {
            lock.release();
        }
    }

    public String getLockKey() {
        return this.lockKey;
    }
}
