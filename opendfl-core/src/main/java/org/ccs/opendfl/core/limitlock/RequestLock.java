package org.ccs.opendfl.core.limitlock;

import org.ccs.opendfl.core.constants.ReqLockType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 接口访问频率控制，用于controller
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

	ReqLockType lockType() default ReqLockType.REDIS;
}