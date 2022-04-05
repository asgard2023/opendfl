package org.ccs.opendfl.core.limitfrequency;

import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 接口访问频率控制，用于controller
 *
 * @author chenjh
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Frequency {

    String name() default "all"; //uniqueKey

    String aliasName() default "";

    int time() default 5; //单位:秒/unit:s

    int limit() default 0;

    int userIpCount() default 0;//支持ip数限制，同一个用户能使用的ip数

    int ipUserCount() default 0;//支持ip数限制，同一个IP能登入的用户数

    String attrName() default "";

    boolean sysconfig() default false;//是否支持系统参数配置

    /**
     * 白名单编码(系统参数)
     *
     * @return
     */
    String whiteCode() default FrequencyConstant.NONE;

    String errMsg() default "";

    String errMsgEn() default "";
}