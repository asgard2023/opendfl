package org.ccs.opendfl.mysql.config;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IFrequencyConfigBiz;
import org.ccs.opendfl.core.biz.IOutLogBiz;
import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
import org.ccs.opendfl.core.biz.IWhiteBlackListBiz;
import org.ccs.opendfl.core.biz.impl.WhiteBlackCheckBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.limitfrequency.FrequencyHandlerInterceptor;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.locks.biz.IRequestLockConfigBiz;
import org.ccs.opendfl.locks.config.RequestLockConfiguration;
import org.ccs.opendfl.locks.limitlock.RequestLockHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * opoendfl for mysql
 *
 * @author chenjh
 */
@Configuration
@Slf4j
public class OpendflMysqlInterceptor extends WebMvcConfigurationSupport {
    @Resource(name = "requestLockHandlerInterceptor")
    private HandlerInterceptor requestLockHandlerInterceptor;

    @Resource(name = "frequencyHandlerInterceptor")
    private HandlerInterceptor frequencyHandlerInterceptor;
    @Resource(name = "outLogMysqlBiz")
    private IOutLogBiz outLogBiz;

    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    @Autowired
    private RequestLockConfiguration requestLockConfiguration;
    @Resource(name = "frequencyConfigMysqlBiz")
    private IFrequencyConfigBiz frequencyConfigBiz;
    @Resource(name = "requestLockConfigMysqlBiz")
    private IRequestLockConfigBiz requestLockConfigBiz;
    @Autowired
    private IWhiteBlackCheckBiz whiteBlackCheckBiz;

    @Resource(name = "whiteBlackListMysqlBiz")
    private IWhiteBlackListBiz whiteBlackListBiz;

    /**
     * 改用mysql的实现
     */
    private void changeMysqlImplement() {
        RequestLockHandlerInterceptor lockHandler = (RequestLockHandlerInterceptor) requestLockHandlerInterceptor;
        log.info("----changeMysqlImplement--requestLockConfigBiz={}", requestLockConfigBiz);
        lockHandler.setRequestLockConfigBiz(requestLockConfigBiz);
        log.info("----changeMysqlImplement--outLogBiz={}", outLogBiz);
        lockHandler.setOutLogBiz(outLogBiz);
        FrequencyUtils.outLogBiz(outLogBiz);

        FrequencyHandlerInterceptor frequencyHandler = (FrequencyHandlerInterceptor) frequencyHandlerInterceptor;
        log.info("----changeMysqlImplement--frequencyConfigBiz={}", frequencyConfigBiz);
        frequencyHandler.setFrequencyConfigBiz(frequencyConfigBiz);

        WhiteBlackCheckBiz checkBiz = (WhiteBlackCheckBiz) whiteBlackCheckBiz;
        log.info("----changeMysqlImplement--whiteBlackListBiz={}", whiteBlackListBiz);
        whiteBlackListBiz.loadInit();
        checkBiz.setWhiteBlackListBiz(whiteBlackListBiz);
        frequencyConfiguration.setWhiteBlackCheckBiz(whiteBlackCheckBiz);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //改用mysql的实现
        changeMysqlImplement();

        if (StringUtils.ifYes(requestLockConfiguration.getIfActive())) {
            log.info("----addInterceptors--RequestLock");
            registry.addInterceptor(requestLockHandlerInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns("/pages/**")
                    .excludePathPatterns("/scripts/**");
        }

        if (StringUtils.ifYes(frequencyConfiguration.getIfActive())) {
            log.info("----addInterceptors--Frequency");
            registry.addInterceptor(frequencyHandlerInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns("/pages/**")
                    .excludePathPatterns("/scripts/**");
        }
        super.addInterceptors(registry);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("----addResourceHandlers--");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
}