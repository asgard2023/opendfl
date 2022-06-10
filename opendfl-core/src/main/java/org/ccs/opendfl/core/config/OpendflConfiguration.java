package org.ccs.opendfl.core.config;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.BaseLimitVo;
import org.ccs.opendfl.core.utils.RequestParams;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "opendfl")
@Configuration
public class OpendflConfiguration {
    private String defaultAttrName="userId";
    private String defaultDeviceId= RequestParams.DEVICE_ID;
    private BaseLimitVo baseLimit=new BaseLimitVo();
}
