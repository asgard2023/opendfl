package org.ccs.opendfl.core.limitfrequency;

import org.ccs.opendfl.core.constants.FreqLimitType;
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
     * 时间，单位秒
     * @return 秒数
     */
    int time() default 5;

    /**
     * 限制次数，为0不限次数
     *
     * @return
     */
    int limit() default 0;

    FreqLimitType freqLimitType() default FreqLimitType.LIMIT;

    /**
     * 可以用于指定为其他的参数，比如account，username等
     * 没有可以不填
     * 但是如果填了属性，那如果属性为空则，直接忽略(接口本身应该有些参数的非空判断，即抛非空异常)，即该频率限制不起作用
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