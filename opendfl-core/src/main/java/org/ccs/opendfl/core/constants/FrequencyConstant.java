package org.ccs.opendfl.core.constants;

/**
 * 常量
 *
 * @author chenjh
 */
public class FrequencyConstant {
    private FrequencyConstant() {

    }

    public static final String FREQ_ERR_MSG = "频率太快了，请稍后再试";
    public static final String FREQ_ERR_MSG_EN = "Frequency is too fast";
    /**
     * none一般用于表示关功关闭或才跟不起作用
     * 主要为了区别null
     */
    public static final String NONE = "none";
    /**
     * 10秒一次读取配置
     */
    public static final int LOAD_CONFIG_INTERVAL = 10000;

    public static final int TIME_MILLISECOND_TO_SECOND=1000;
    public static final int TIME_MILLISECOND_TO_MINUTE=TIME_MILLISECOND_TO_SECOND*60;
    public static final int TIME_MILLISECOND_TO_HOUR=TIME_MILLISECOND_TO_SECOND*3600;
    public static final int TIME_MILLISECOND_TO_DAY=TIME_MILLISECOND_TO_HOUR*24;
}
