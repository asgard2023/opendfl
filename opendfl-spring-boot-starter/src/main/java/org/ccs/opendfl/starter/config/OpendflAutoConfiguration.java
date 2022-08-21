package org.ccs.opendfl.starter.config;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
//import org.ccs.opendfl.locks.config.RequestLockConfiguration;
import org.ccs.opendfl.starter.service.LogicHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * starter管理类
 *
 * @author JustryDeng
 * @date 2022-07-17 06:48:27
 */
@Configuration
@EnableConfigurationProperties({FrequencyConfiguration.class
//        , RequestLockConfiguration.class
})
@ConditionalOnProperty(value = "frequency.ifActive", havingValue = "1")
@ComponentScan(basePackages = {"org.ccs.opendfl"})
public class OpendflAutoConfiguration {

    @Bean
    public LogicHandler logicHandler() {
        LogicHandler logicHandler = new LogicHandler();
        return logicHandler;
    }
}
