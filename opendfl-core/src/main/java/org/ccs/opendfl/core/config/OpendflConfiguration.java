package org.ccs.opendfl.core.config;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.BaseLimitVo;
import org.ccs.opendfl.core.utils.RequestParams;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * opendfl配置
 *
 * @author chenjh
 */
@Data
@ConfigurationProperties(prefix = "opendfl")
@Configuration
public class OpendflConfiguration {
    /**
     * 支持关闭这个服务的Redis配置，由外其他类提供
     */
    private String redisCacheEnable="1";
    /**
     * 默认attrName
     */
    private String defaultAttrName="userId";
    private String defaultDeviceId= RequestParams.DEVICE_ID;
    private BaseLimitVo baseLimit=new BaseLimitVo();
}
