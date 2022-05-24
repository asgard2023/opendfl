package org.ccs.opendfl.mysql.base;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component  
public class InjectBeanSelfProcessor implements BeanPostProcessor, ApplicationContextAware {  
    private ApplicationContext context;  
    //① 注入ApplicationContext  
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {  
        this.context = applicationContext;  
    }  
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {  
        if(!(bean instanceof ISelfInject)) { //② 如果Bean没有实现ISelfInject标识接口 跳过  
            return bean;  
        }  
        if(AopUtils.isAopProxy(bean)) { //③ 如果当前对象是AOP代理对象，直接注入  
            ((ISelfInject) bean).setSelf(bean);  
        } else {  
            //④ 如果当前对象不是AOP代理，则通过context.getBean(beanName)获取代理对象并注入  
            //此种方式不适合解决prototype Bean的代理对象注入  
            ((ISelfInject)bean).setSelf(context.getBean(beanName));  
        }  
        return bean;  
    }  
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {  
        return bean;  
    }  
}  