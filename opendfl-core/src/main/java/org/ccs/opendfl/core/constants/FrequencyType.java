package org.ccs.opendfl.core.constants;

/**
 * 频率限制类型
 *
 * @author chenjh
 */
public enum FrequencyType {
    /**
     * 一级
     *
     * @Frquency注解限制
     */
    FREQUENCY("frequency"),
    /**
     * 二级
     *
     * @Frquency2注解限制
     */
    FREQUENCY2("frequency2"),
    /**
     * 三级限制
     *
     * @Frquency3注解限制
     */
    FREQUENCY3("frequency3"),
    /**
     * 三级限制
     *
     * @Frquency4注解限制
     */
    FREQUENCY4("frequency4"),
    /**
     * 三级限制
     *
     * @Frquency5注解限制
     */
    FREQUENCY5("frequency5"),
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
