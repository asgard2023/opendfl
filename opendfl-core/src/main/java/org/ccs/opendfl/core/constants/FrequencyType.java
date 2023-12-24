package org.ccs.opendfl.core.constants;

/**
 * 频率限制类型
 *
 * @author chenjh
 */
public enum FrequencyType {
    /**
     * 一级
     * <p>
     * 注解：@Frequency限制
     */
    FREQUENCY("frequency"),
    /**
     * 不走@Frequency注解限制，而走uri配置限制
     */
    URI_CONFIG("uriConfig");
    private final String type;

    FrequencyType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
