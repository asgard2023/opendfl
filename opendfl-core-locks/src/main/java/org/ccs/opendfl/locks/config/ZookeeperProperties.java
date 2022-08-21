package org.ccs.opendfl.locks.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * zookeeper 配置
 * @author chenjh
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "spring.zookeeper")
public class ZookeeperProperties {
    private int baseSleepTimeMs;
    private int maxRetries;
    private String address;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
}
