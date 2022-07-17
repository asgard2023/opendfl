package org.ccs.opendfldemo;

import lombok.RequiredArgsConstructor;
import org.ccs.opendfl.starter.service.LogicMysqlHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * opendfl-start demo
 *
 * @author chenjh
 * @date 2022/7/6 09:47:38
 */
@SpringBootApplication
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.ccs.opendfl", "org.ccs.opendfl.mysql"})
public class MysqlSpringBootDemoApplication implements ApplicationRunner {

    private final LogicMysqlHandler logicMysqlHandler;

    public static void main(String[] args) {
        SpringApplication.run(MysqlSpringBootDemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        logicMysqlHandler.handle();
    }
}
