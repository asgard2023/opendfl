package org.ccs.opendfldemo;

import lombok.RequiredArgsConstructor;
import org.ccs.opendfl.starter.service.LogicHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * opendfl-start demo
 * @author chenjh
 * @date 2022/7/6 09:47:38
 */
@SpringBootApplication
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.ccs.opendfl"})
public class SpringBootDemoApplication implements ApplicationRunner {

    private final LogicHandler logicHandler;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        logicHandler.handle();
    }
}
