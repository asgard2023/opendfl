package org.ccs.opendfl.console.vo;

import lombok.Data;

@Data
public class FrequencyLimitVo {
    /**
     *
     */
    private String uri;

    private String method;


//    /**
//     * 编码
//     */
//    private String code;
//
//    /**
//     * 名称
//     */
//    private String name;
//
//    /**
//     * 别名，用于多个接口共用一个限制
//     */
//    private String alias;

    /**
     * 时间间隔
     */
    private Integer time;

    /**
     * 限制次数
     */
    private Integer limitCount;

    /**
     * 一个用户允许IP个数
     */
    private String freqLimitType;

//    /**
//     * 白名单编码
//     */
//    private String whiteCode;

    /**
     * 限制类型
     */
    private String limitType;

    /**
     * 限制属性名
     */
    private String attrName;

    /**
     * 是否需要登入
     */
    private Integer needLogin;

    private Integer log;

    /**
     * 异常消息
     */
    private String errMsg;

    /**
     * 异常消息en
     */
    private String errMsgEn;
}
