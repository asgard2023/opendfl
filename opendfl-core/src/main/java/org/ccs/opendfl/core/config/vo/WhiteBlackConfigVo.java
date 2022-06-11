package org.ccs.opendfl.core.config.vo;

import lombok.Data;

@Data
public class WhiteBlackConfigVo {
    private String items;
    private String ips;
    private String users;
    private String origins;
    /**
     * 是否必填deviceId，如果是，deviceId为空，拒绝，否则忽略
     */
    private Character ifDeviceIdRequire;
    private Character ifOriginRequire;
    private String deviceIds;
}
