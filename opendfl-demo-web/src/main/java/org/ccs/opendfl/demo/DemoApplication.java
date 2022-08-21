package org.ccs.opendfl.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author chenjh
 */
@SpringBootApplication(scanBasePackages = {"org.ccs.opendfl.core"
//        , "org.ccs.opendfl.locks"
        , "org.ccs.opendfl.demo"})
@EnableConfigurationProperties
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
