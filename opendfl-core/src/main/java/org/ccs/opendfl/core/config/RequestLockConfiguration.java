package org.ccs.opendfl.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "requestlock")
@Component("requestLockConfiguration")
@Data
public class RequestLockConfiguration {
    private String redisPrefix="limitLock";
    private Character ifActive='1';
}
