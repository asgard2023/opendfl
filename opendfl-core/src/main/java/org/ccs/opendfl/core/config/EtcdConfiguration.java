package org.ccs.opendfl.core.config;

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
        log.info("----etcdClient--etcd.endpoints={}", endpoints);
        if (StringUtils.isBlank(endpoints)) {
            log.warn("----etcdClient--disabled");
            return null;
        }
        String[] urls = endpoints.split(",");
        return Client.builder().endpoints(urls).build();
    }
}
