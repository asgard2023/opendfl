package org.ccs.opendfl.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 用于starter支持配置开关，比如不加载RedisConfiguration
 *
 * @author chenjh
 */
@Data
@ConfigurationProperties(prefix = "starter")
@Configuration
public class StarterConfiguration {
    /**
     * 支持关闭这个服务的Redis配置，由外其他类提供
     */
    private String redisCacheEnable="1";
}
