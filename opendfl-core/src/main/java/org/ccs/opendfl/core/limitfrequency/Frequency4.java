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
public @interface Frequency4 {

    String name() default "all";

    String aliasName() default "";

    /**
     * 时间，单位秒
     * @return 秒数
     */
    int time() default 86400;

    int limit() default 0;

    FreqLimitType freqLimitType() default FreqLimitType.LIMIT;

    /**
     * 可以用于指定为其他的参数，比如account，username等
     * 没有可以不填
     * 但是如果填了属性，那如果属性为空则，直接忽略(接口本身应该有些参数的非空判断，即抛非空异常)，即该频率限制不起作用
     * @return attrName
     */
    String attrName() default "";

    /**
     * 是否支持系统参数配置
     * @return 是否系统配置
     */
    boolean sysconfig() default false;

    /**
     * 是否需要登入
     *
     */
    boolean needLogin() default false;
    /**
     * 是否显示日志
     * @return 是否
     */
    boolean log() default false;

    /**
     * 白名单编码
     *
     */
    String whiteCode() default FrequencyConstant.NONE;

    String errMsg() default "";

    String errMsgEn() default "";
}