package org.ccs.opendfl.core;

import org.ccs.opendfl.core.utils.CommUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommUtilsTest {
    @Test
    void getDomain(){
        String url="https://www.baidu.com/s?ie=UTF-8&wd=%E4%BE%9D%E4%BA%BA%E5%AE%B6%E7%BA%BA";
        url= CommUtils.getDomain(url);
        Assertions.assertEquals("www.baidu.com", url);
        url="http://175.178.252.112:8080/index.html";
        url= CommUtils.getDomain(url);
        Assertions.assertEquals("175.178.252.112", url);
    }

    @Test
    void getStringLimit(){
        String str = "abcd12345";
        System.out.println(CommUtils.getStringLimit(str, 3));
        System.out.println(CommUtils.getStringLimit(str, -3));
    }
}
