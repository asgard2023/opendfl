package org.ccs.opendfl.core.constants;

public class FrequencyConstant {
    private FrequencyConstant() {

    }

    public static final String FREQ_ERR_MSG="频率太快了，请稍后再试";
    public static final String FREQ_ERR_MSG_EN="Frequency is too fast";
    public static final String NONE="none";

    public static enum whiteCodes {
        NONE("none", "无", "none"),//即没有白名单
        LIMIT_TEST("limit:test", "频率限制白名单-测试", "none"),//用于已经触发频率限制时，如果在白名单中则不限制
        LIMIT_LOGIN("limit:login", "频率限制白名单-登入", "none"),//用于已经触发频率限制时，如果在白名单中则不限制
        LIMIT_REPLAY("limit:replay", "频率限制白名单-回放", "none");//用于已经触发频率限制时，如果在白名单中则不限制
        private String code;
        private String codeName;
        private String defaultValue;

        whiteCodes(String code, String codeName, String defaultValue) {
            this.code = code;
            this.codeName = codeName;
            this.defaultValue = defaultValue;
        }

        public String getCode() {
            return code;
        }

        public String getCodeName() {
            return codeName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }
}
