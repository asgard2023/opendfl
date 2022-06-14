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
public @interface Frequency2 {

    String name() default "all"; //uniqueKey

    String aliasName() default "";

    int time() default 3600; //单位:秒/unit:s

    int limit() default 0;

    /**
     * 支持ip数限制，同一个用户能使用的ip数
     * @return
     */
    int userIpCount() default 0;

    /**
     * 支持ip数限制，同一个IP能登入的用户数
     * @return
     */
    int ipUserCount() default 0;

    String attrName() default "";

    /**
     * 是否支持系统参数配置
     * @return
     */
    boolean sysconfig() default false;

    /**
     * 是否资源级限制，会取attrName对应的值，对该资源进行限制
     * resource=true && ipUserCount>0时，IP资源限制，即ip+attrName来计数，limit=ipUserCount
     * resource=true && ipUserCount=0时，用户资源限制，即userId+attrName来计数，限制limit=limit
     * resource=false时，非资源限制
     * @return
     */
    boolean resource() default false;

    /**
     * 是否需要登入
     *
     */
    boolean needLogin() default false;

    /**
     * 白名单编码
     *
     */
    String whiteCode() default FrequencyConstant.NONE;

    String errMsg() default "";

    String errMsgEn() default "";
}