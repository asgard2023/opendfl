package org.ccs.opendfl.core.constants;

/**
 * 分布式锁类型
 * @author chenjh
 */
public enum ReqLockType {
    REDIS("reids"),
    ETCD("etcd"),
    ZK("zk");
    private String type;
    ReqLockType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
