package org.ccs.opendfl.core.constants;

/**
 * 数据来源
 *
 * @author chenjh
 */
public enum DataSourceType {
    REDIS("redis"),
    ETCD("etcd"),
    ZK("zk");

    private final String type;
    DataSourceType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
