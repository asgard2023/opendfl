package org.ccs.opendfl.core.constants;

/**
 * 请求系统类型
 *
 * @author chenjh
 */
public enum ReqSysType {
    /**
     * ios
     */
    IOS('i', "ios"),
    /**
     * android
     */
    ANDROID('a', "android"),
    /**
     * h5
     */
    H5('h', "h5"),
    /**
     * weixin微信
     */
    WX('w', "wx"),
    /**
     * 电脑
     */
    PC('p', "pc");
    private final Character type;
    private final String code;

    ReqSysType(Character type, String code) {
        this.type = type;
        this.code = code;
    }

    public Character getType() {
        return this.type;
    }

    public String getCode() {
        return code;
    }

    public static ReqSysType parse(Character type) {
        if (type == null) {
            return null;
        }
        ReqSysType[] types = ReqSysType.values();
        for (ReqSysType sysType : types) {
            if (sysType.type.equals(type)) {
                return sysType;
            }
        }
        return null;
    }

    public static ReqSysType parseCode(String code) {
        if (code == null) {
            return null;
        }
        ReqSysType[] types = ReqSysType.values();
        for (ReqSysType sysType : types) {
            if (sysType.code.equals(code)) {
                return sysType;
            }
        }
        return null;
    }

    public static Character getSysType(String code) {
        ReqSysType reqSysType = parseCode(code);
        if (reqSysType != null) {
            return reqSysType.getType();
        }
        return null;
    }
}
