package org.ccs.opendfl.core.config;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.LimitConfigVo;
import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author justin
 */
@Configuration
@ConfigurationProperties(prefix = "frequency")
@Component("frequencyConfiguration")
@Data
public class FrequencyConfiguration {
    private Character ifActive='1';
    private String redisPrefix="limitCount";
    private Map<String, String> whiteCodeUsers;
    private Integer initLogCount=100;
    /**
     * 黑名单配置
     */
    private WhiteBlackConfigVo black;
    /**
     * 白名单配置
     */
    private WhiteBlackConfigVo white;
    /**
     * 频率限制
     */
    private LimitConfigVo limit;
}
