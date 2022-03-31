package org.ccs.opendfl.core.utils;

import org.ccs.opendfl.core.biz.IUserBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.OpendflConfiguration;
import org.ccs.opendfl.core.limitcount.FrequencyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public final class RequestParams {
    private RequestParams(){

    }
    private static OpendflConfiguration opendflConfiguration;


    @Autowired
    public void setOpendflConfiguration(OpendflConfiguration opendflConfiguration) {
        RequestParams.opendflConfiguration = opendflConfiguration;
        RequestParams.USER_ID=opendflConfiguration.getDefaultAttrName();
    }
    /**
     * 用户ID
     */
    public static String USER_ID = "userId";
    /**
     * username
     */
    public static final String USERNAME = "username";
    /**
     * APP包名
     */
    public static final String PKG = "pkg";

    /**
     * 语言
     */
    public static final String LANG = "lang";
    
    public static final String AUTHORIZATION = "Authorization";
}
