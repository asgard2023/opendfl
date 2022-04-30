package org.ccs.opendfl.core.constants;

/**
 * 分布式锁类型
 * @author chenjh
 */
public enum ReqLockType {
    /**
     * Redis分布式锁，ifAbsent已存在时快速失败
     */
    REDIS("reids", "redis"),
    /**
     * ETCD分布式锁，ifAbsent已存在时快速失败
     */
    ETCD("etcd", "etcd"),
    /**
     * ETCD分布式锁，同步模式，等待前面任务锁消失或完成，然后取到锁
     */
    ETCD_SYNC("etcdSync", "etcd"),
    /**
     * ETCD分布式锁，异步模式，暂时觉得没有必要，未实现
     * @Deprecated
     */
    ETCD_ASYNC("etcdAsync", "etcd"),
    /**
     * zk分布式锁，ifAbsent已存在时快速失败
     */
    ZK("zk", "zk");
    private String type;
    private String source;
    ReqLockType(String type, String source){
        this.type = type;
        this.source = source;
    }

    public String getType(){
        return this.type;
    }

    public String getSource(){
        return this.source;
    }
}
