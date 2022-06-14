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

    /**
     * 编码，uniqueKey
     *
     * @return
     */
    String name() default "all";

    /**
     * 只相当于分组，没有实际作用，允许重复
     *
     * @return
     */
    String aliasName() default "";

    /**
     * 单位:秒/unit:s
     *
     * @return
     */
    int time() default 5;

    /**
     * 限制次数，为0不限次数
     *
     * @return
     */
    int limit() default 0;

    /**
     * 支持ip数限制，同一个用户能使用的ip数
     *
     * @return
     */
    int userIpCount() default 0;

    /**
     * 支持ip数限制，同一个IP能登入的用户数
     *
     * @return
     */
    int ipUserCount() default 0;

    /**
     * 方法参数名，默认userId
     * 可以用于指定为其他的参数，比如account，username等
     * @return
     */
    String attrName() default "";

    /**
     * 是否支持系统参数配置
     *
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
     * @return
     */
    boolean needLogin() default false;

    /**
     * 方法级用户白名单
     *
     * @return
     */
    String whiteCode() default FrequencyConstant.NONE;

    String errMsg() default "";

    String errMsgEn() default "";
}