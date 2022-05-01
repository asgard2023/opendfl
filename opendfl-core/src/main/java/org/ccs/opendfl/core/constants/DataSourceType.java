package org.ccs.opendfl.core.constants;

public enum DataSourceType {
    REDIS("redis"),
    ETCD("etcd"),
    ZK("zk");

    private String type;
    DataSourceType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
