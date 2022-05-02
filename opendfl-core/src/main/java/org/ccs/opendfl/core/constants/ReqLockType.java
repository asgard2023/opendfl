package org.ccs.opendfl.core.constants;

/**
 * 分布式锁类型
 * @author chenjh
 */
public enum ReqLockType {
    /**
     * Redis分布式锁，ifAbsent已存在时快速失败
     */
    REDIS("reids", DataSourceType.REDIS.getType()),
    /**
     * ETCD分布式锁，ifAbsent已存在时快速失败
     */
    ETCD_KV("etcdKv", DataSourceType.ETCD.getType()),
    /**
     * ETCD分布式锁，同步模式，等待前面任务锁消失或完成，然后取到锁
     */
    ETCD_LOCK("etcdLock", DataSourceType.ETCD.getType()),
    /**
     * ETCD分布式锁，异步模式，暂时觉得没有必要，未实现
     * @Deprecated
     */
    ETCD_ASYNC("etcdAsync", DataSourceType.ETCD.getType()),
    /**
     * zk分布式锁，ifAbsent已存在时快速失败
     */
    ZK("zk", DataSourceType.ZK.getType());
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
