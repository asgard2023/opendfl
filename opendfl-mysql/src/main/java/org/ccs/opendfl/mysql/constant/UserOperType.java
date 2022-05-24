package org.ccs.opendfl.mysql.constant;

/**
 * 控制台功能操作类型
 *
 * @author chenjh
 */
public enum UserOperType {
    /**
     * 查看权限
     */
    VIEW("view"),
    /**
     * 删除限制缓存
     */
    EVICT("evict"),
    /**
     * 清除list
     */
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
