package org.ccs.opendfl.console.constant;

/**
 * 控制台功能操作类型
 *
 * @author chenjh
 */
public enum UserOperType {
    VIEW("view"),
    EVICT("evict"),
    CLEAR("clear"),
    LIST("list");
    private final String type;

    UserOperType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

}
