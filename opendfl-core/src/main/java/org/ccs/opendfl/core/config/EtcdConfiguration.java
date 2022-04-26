package org.ccs.opendfl.core.config;

import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * etcd config
 * @author chenjh
 */
@Configuration
public class EtcdConfiguration {
    @Value("${spring.etcd.endpoints}")
    private String endpoints = "http://localhost:2379";

    @Bean
    public Client etcdClient() {
        String[] urls = endpoints.split(",");
        return Client.builder().endpoints(urls).build();
    }
}
