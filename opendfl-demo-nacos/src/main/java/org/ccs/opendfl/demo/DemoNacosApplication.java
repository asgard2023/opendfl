package org.ccs.opendfl.demo;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.OpendflConfiguration;
import org.ccs.opendfl.core.config.RequestLockConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author chenjh
 */
@SpringBootApplication(scanBasePackages = {"org.ccs.opendfl.core", "org.ccs.opendfl.demo"})
@EnableConfigurationProperties()
public class DemoNacosApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoNacosApplication.class, args);
    }
}
