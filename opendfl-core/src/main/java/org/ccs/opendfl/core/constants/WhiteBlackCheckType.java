package org.ccs.opendfl.core.constants;

/**
 * 黑名单，白名单检查类型
 */
public enum WhiteBlackCheckType {
    /**
     * 用户
     */
    USER(1, "user"),
    /**
     * IP
     */
    IP(2, "ip"),
    /**
     * 设备号
     */
    DEVICE(3, "device");

    private Integer type;
    private String code;
    WhiteBlackCheckType(Integer type, String code){
        this.type = type;
        this.code = code;
    }

    public Integer getType(){
        return type;
    }

    public String getCode() {
        return code;
    }
}
