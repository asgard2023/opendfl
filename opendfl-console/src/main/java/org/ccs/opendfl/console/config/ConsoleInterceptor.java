package org.ccs.opendfl.console.config;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.locks.config.RequestLockConfiguration;
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
 * @author chenjh
 */
@Configuration
@Slf4j
public class ConsoleInterceptor extends WebMvcConfigurationSupport {
    @Resource(name = "requestLockHandlerInterceptor")
    private HandlerInterceptor requestLockHandlerInterceptor;

    @Resource(name = "frequencyHandlerInterceptor")
    private HandlerInterceptor frequencyHandlerInterceptor;

    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    @Autowired
    private RequestLockConfiguration requestLockConfiguration;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (StringUtils.ifYes(frequencyConfiguration.getIfActive())) {
            log.info("----addInterceptors--Frequency");
            registry.addInterceptor(frequencyHandlerInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns("/pages/**")
                    .excludePathPatterns("/scripts/**");
        }
        if (StringUtils.ifYes(requestLockConfiguration.getIfActive())) {
            log.info("----addInterceptors--RequestLock");
            registry.addInterceptor(requestLockHandlerInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns("/pages/**")
                    .excludePathPatterns("/scripts/**");
        }
    }

    /**
     * 默认index.html
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("----addResourceHandlers--");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/resources/");
        super.addResourceHandlers(registry);
    }
}