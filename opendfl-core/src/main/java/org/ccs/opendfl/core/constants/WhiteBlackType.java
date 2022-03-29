package org.ccs.opendfl.core.constants;

public enum WhiteBlackType {
    /**
     * 白名单
     */
    Frequency(1, "frequency"),
    /**
     * 白名单
     */
    WHITE(2, "white"),
    /**
     * 黑名单
     */
    BLACK(3, "black");

    private Integer type;
    private String code;
    WhiteBlackType(Integer type, String code){
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
