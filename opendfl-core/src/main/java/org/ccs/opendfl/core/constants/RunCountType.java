package org.ccs.opendfl.core.constants;

/**
 * 接口调用次数类型
 *
 * @author chenjh
 */
public enum RunCountType {
    COUNT("count", "调用总次数"),
    OUT_LIMIT("outLimit", "超限总次数");

    private String type;
    private String typeName;

    RunCountType(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public String getCode() {
        return this.type;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static RunCountType parse(String type) {
        if (type == null) {
            return null;
        }
        RunCountType[] types = RunCountType.values();
        for (RunCountType countType : types) {
            if (countType.type.equals(type)) {
                return countType;
            }
        }
        return null;
    }

}
