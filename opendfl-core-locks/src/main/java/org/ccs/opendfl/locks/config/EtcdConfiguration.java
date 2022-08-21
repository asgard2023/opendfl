package org.ccs.opendfl.locks.config;

import io.etcd.jetcd.Client;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.utils.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * etcd config
 * @author chenjh
 */
@Configuration
@ConfigurationProperties(prefix = "spring.etcd")
@Slf4j
@Data
public class EtcdConfiguration {
    private String endpoints;

    @Bean(name="etcdClient")
    public Client etcdClient() {
        if (StringUtils.isBlank(endpoints)) {
            log.warn("----etcdClient--etcd.endpoints={} empty disabled", endpoints);
            return null;
        }
        try {
            log.info("----etcdClient--etcd.endpoints={}", endpoints);
            String[] urls = endpoints.split(",");
            return Client.builder().endpoints(urls).build();
        } catch (Exception e) {
            log.error("----etcdClient--etcd.endpoints={} invalid disabled {}", endpoints, e.getMessage());
            return null;
        }
    }
}
