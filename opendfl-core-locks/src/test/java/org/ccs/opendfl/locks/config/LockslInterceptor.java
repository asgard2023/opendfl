package org.ccs.opendfl.locks.config;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.filter.HttpServletRequestReplacedFilter;
import org.ccs.opendfl.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * @author chenjh
 */
@Configuration
@Slf4j
public class LockslInterceptor extends WebMvcConfigurationSupport {
    @Resource(name = "requestLockHandlerInterceptor")
    private HandlerInterceptor requestLockHandlerInterceptor;

    @Autowired
    private RequestLockConfiguration requestLockConfiguration;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (StringUtils.ifYes(requestLockConfiguration.getIfActive())) {
            log.info("----addInterceptors--RequestLock");
            registry.addInterceptor(requestLockHandlerInterceptor)
                    .addPathPatterns("/**");
        }
    }

    @Bean
    public FilterRegistrationBean<HttpServletRequestReplacedFilter> httpServletRequestReplacedRegistration() {
        FilterRegistrationBean<HttpServletRequestReplacedFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new HttpServletRequestReplacedFilter());
        registration.addUrlPatterns("/*");
        registration.setName("httpServletRequestReplacedFilter");
        registration.setOrder(1);
        return registration;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("----addResourceHandlers--");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/scripts/**").addResourceLocations("classpath:/scripts/");
        super.addResourceHandlers(registry);
    }
}