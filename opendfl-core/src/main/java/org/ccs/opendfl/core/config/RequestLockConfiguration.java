package org.ccs.opendfl.core.config;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.RequestLockConfigVo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "requestlock")
@Component("requestLockConfiguration")
@Data
@RefreshScope
public class RequestLockConfiguration {
    private String redisPrefix="limitLock";
    private Character ifActive='1';
    private List<RequestLockConfigVo> lockConfigs;
}
