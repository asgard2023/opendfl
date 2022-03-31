package org.ccs.opendfl.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.config.OpendflConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public final class RequestParams {
    private RequestParams(){

    }
    private static OpendflConfiguration opendflConfiguration;


    @Autowired
    public void setOpendflConfiguration(OpendflConfiguration opendflConfiguration) {
        RequestParams.opendflConfiguration = opendflConfiguration;
        if(StringUtils.isNotBlank(opendflConfiguration.getDefaultAttrName())) {
            RequestParams.USER_ID = opendflConfiguration.getDefaultAttrName();
            log.info("-----USER_ID use default attrName={}", RequestParams.USER_ID);
        }
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
