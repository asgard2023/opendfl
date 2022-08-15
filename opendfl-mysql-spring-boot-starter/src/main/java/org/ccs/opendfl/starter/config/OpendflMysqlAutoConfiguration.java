package org.ccs.opendfl.starter.config;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.RequestLockConfiguration;
import org.ccs.opendfl.starter.service.LogicMysqlHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * starter管理类
 */
@Configuration
@EnableConfigurationProperties({FrequencyConfiguration.class
        , RequestLockConfiguration.class})
@ConditionalOnProperty(value = "frequency.ifActive", havingValue = "1")
@ComponentScan(basePackages = {"org.ccs.opendfl","org.ccs.opendfl.mysql"})
public class OpendflMysqlAutoConfiguration {

    @Bean
    public LogicMysqlHandler logicHandler() {
        LogicMysqlHandler logicHandler= new LogicMysqlHandler();
        return logicHandler;
    }
}
