package org.ccs.opendfl.core.limitlock;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 接口访问频率控制，用于controller
 * 不支持json参数，因为基于流处理时，不能在拦截器中读流，否则接口本身就读不了
 * @author chenjh
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RequestLock {

	String name() default "all"; //uniqueKey

	int time() default 10; //单位:秒/unit:s

	String attrName() default "";

	String errMsg() default "";
}