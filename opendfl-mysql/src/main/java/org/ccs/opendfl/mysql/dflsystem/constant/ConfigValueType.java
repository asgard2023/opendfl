package org.ccs.opendfl.mysql.dflsystem.constant;

/**
 * 系统参数的值配置类型
 *
 * @author chenjh
 */
public enum ConfigValueType {
    INT(1, "Integer"),
    STRING(2, "String"),
    JSON(3, "json");
    private final Integer type;
    private final String code;

    ConfigValueType(Integer type, String code) {
        this.type = type;
        this.code = code;
    }

    public Integer getType() {
        return this.type;
    }

    public String getCode() {
        return this.code;
    }

    public static <E> E getValue(final ConfigValueType valueType, final String value) {
        if (value == null) {
            return null;
        }
        if (ConfigValueType.INT == valueType) {
            Integer valueInt = Integer.valueOf(value);
            return (E) valueInt;
        }
        return (E) value;
    }
}
