package org.ccs.opendfl.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "opendfl")
@Configuration
public class OpendflConfiguration {
    private String defaultAttrName="userId";
}
