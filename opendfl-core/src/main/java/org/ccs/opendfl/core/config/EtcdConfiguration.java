package org.ccs.opendfl.core.config;

import io.etcd.jetcd.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * etcd config
 * @author chenjh
 */
@Configuration
@Slf4j
public class EtcdConfiguration {
    @Value("${spring.etcd.endpoints}")
    private String endpoints = "http://localhost:2379";
    @Autowired
    private RequestLockConfiguration requestLockConfiguration;

    @Bean(name="etcdClient")
    public Client etcdClient() {
        log.info("----etcdClient--ifEtcdSyncLock={} etcd.endpoints={}", requestLockConfiguration.getIfEtcdSyncLock(), endpoints);
        String[] urls = endpoints.split(",");
        return Client.builder().endpoints(urls).build();
    }
}
