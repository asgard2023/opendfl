package org.ccs.opendfl.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author chenjh
 */
@SpringBootApplication(scanBasePackages = {"org.ccs.opendfl.core", "org.ccs.opendfl.locks", "org.ccs.opendfl.mysql"})
@EnableConfigurationProperties
@MapperScan(basePackages = "org.ccs.opendfl.mysql")
public class MysqlApplication {
    public static void main(String[] args) {
        SpringApplication.run(MysqlApplication.class, args);
    }
}
