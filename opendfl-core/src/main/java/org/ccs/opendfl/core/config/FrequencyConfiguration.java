package org.ccs.opendfl.core.config;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.LimitConfigVo;
import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.utils.CommUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 频率限制配置
 * @author chenjh
 */
@Configuration
@ConfigurationProperties(prefix = "frequency")
@Component("frequencyConfiguration")
@Data
public class FrequencyConfiguration {
    private Character ifActive='1';
    private String redisPrefix="limitCount";
    private Map<String, String> whiteCodeUsers;
    /**
     * 启动时每个接口前100次接口调用白名单可输出日志
     */
    private Integer initLogCount=100;
    /**
     * 记录慢接口时间(毫秒)
     */
    private Long minRunTime=500L;
    /**
     * 黑名单配置
     */
    private WhiteBlackConfigVo black;
    public void setBlack(WhiteBlackConfigVo black) {
        black.setIps(CommUtils.appendComma(black.getIps()));
        this.black = black;
    }
    /**
     * 白名单配置
     */
    private WhiteBlackConfigVo white;
    public void setWhite(WhiteBlackConfigVo white) {
        white.setIps(CommUtils.appendComma(white.getIps()));
        this.white = white;

    }
    /**
     * 频率限制
     */
    private LimitConfigVo limit;
}
