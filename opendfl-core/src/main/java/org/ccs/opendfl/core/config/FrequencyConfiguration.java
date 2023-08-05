package org.ccs.opendfl.core.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
import org.ccs.opendfl.core.config.vo.LimitConfigVo;
import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
public class FrequencyConfiguration {
    private Character ifActive = '1';
    private String redisPrefix = "limitCount";
    private boolean async=true;
    /**
     * 是否开启频率限制key hash以压缩key长度
     */
    private Integer ifKeyHash=0;
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
    private Integer maxRunTimeInterval = 30;
    /**
     * 是否启动执行时长监控
     */
    private Character runTimeMonitor = '0';
    /**
     * 接口调用次数缓存zset保存天数
     * 为0不保存
     */
    private Integer runCountCacheDay = 7;

    /**
     * IP限制数与limit的比率，如果2表示是2倍
     */
    private Float limitIpRate=2F;
    /**
     * 黑名单配置
     */
    private WhiteBlackConfigVo black;

    @Autowired
    private IWhiteBlackCheckBiz whiteBlackCheckBiz;
    public void setWhiteBlackCheckBiz(IWhiteBlackCheckBiz whiteBlackCheckBiz){
        this.whiteBlackCheckBiz = whiteBlackCheckBiz;
    }
    public IWhiteBlackCheckBiz getWhiteBlackCheckBiz() {
        return this.whiteBlackCheckBiz;
    }

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
