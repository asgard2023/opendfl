package org.ccs.opendfl.core.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.config.vo.LimitConfigVo;
import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 频率限制配置
 *
 * @author chenjh
 */
@Configuration
@ConfigurationProperties(prefix = "frequency")
@Component("frequencyConfiguration")
@Data
@Slf4j
public class FrequencyConfiguration {
    private Character ifActive = '1';
    private String redisPrefix = "limitCount";
    private Map<String, String> whiteCodeUsers;
    /**
     * 启动时每个接口前100次接口调用白名单可输出日志
     */
    private Integer initLogCount = 100;
    /**
     * 记录慢接口时间(毫秒)
     */
    private Long minRunTime = 500L;
    /**
     * 记录慢接口时间(毫秒)
     */
    private Integer maxRunTimeInterval=30;
    /**
     * 是否启动执行时长监控
     */
    private Character runTimeMonitor='0';
    /**
     * 黑名单配置
     */
    private WhiteBlackConfigVo black;

    public void setBlack(WhiteBlackConfigVo black) {
        String ipsOld = black.getIps();
        black.setIps(RequestUtils.getIpConvertNums(black.getIps()));
        log.info("----setBlack--ipsOld={} ips={}", ipsOld, black.getIps());
        this.black = black;
    }

    /**
     * 白名单配置
     */
    private WhiteBlackConfigVo white;

    public void setWhite(WhiteBlackConfigVo white) {
        String ipsOld = black.getIps();
        white.setIps(RequestUtils.getIpConvertNums(white.getIps()));
        log.info("----setWhite--ipsOld={} ips={}", ipsOld, white.getIps());
        this.white = white;
    }

    /**
     * 频率限制
     */
    private LimitConfigVo limit;
}
