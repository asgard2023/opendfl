package org.ccs.opendfl.mysql;

import com.alibaba.fastjson.JSON;
import org.ccs.opendfl.core.utils.AnnotationControllerUtils;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.vo.RequestVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

class RequestUtilsTest {
    @Test
    void isIpAddress() {

        System.out.println(StandardCharsets.UTF_8.name());
        String host;
        boolean isIpAddress;
        long time = System.currentTimeMillis();

        host = "192.168.0.101";
        isIpAddress = RequestUtils.isIpAddress(host);
        Assertions.assertTrue(isIpAddress, host + " ipv4 valid");
        System.out.println(System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        host = "fe80::ce24:2eff:fef2:75e8";
        isIpAddress = RequestUtils.isIpv6Address(host);
        Assertions.assertTrue(isIpAddress, host + " ipv6 valid");
        System.out.println(System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        host = "2409:8a55:3312:c4e0:ce24:2eff:fef3:75e2";
        isIpAddress = RequestUtils.isIpv6Address(host);
        Assertions.assertTrue(isIpAddress, host + " ipv6 valid");
        System.out.println(System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        host = "123.123.123.aa";
        isIpAddress = RequestUtils.isIpAddress(host);
        Assertions.assertFalse(isIpAddress, host + " invalid");
        System.out.println(System.currentTimeMillis() - time);

    }

    @Test
    void getNumConvertIp() {
        String host;
        host = "192.168.0.101";
        long ipNum = RequestUtils.getIpConvertNum(host);
        System.out.println("host=" + host + " " + " ipNum=" + ipNum);
        Assertions.assertEquals(host, RequestUtils.getNumConvertIp(ipNum));

        host = "192.168.197.101";
        ipNum = RequestUtils.getIpConvertNum(host);
        System.out.println("host=" + host + " " + " ipNum=" + ipNum);
        Assertions.assertEquals(host, RequestUtils.getNumConvertIp(ipNum));

        host = "10.0.158.116";
        ipNum = RequestUtils.getIpConvertNum(host);
        System.out.println("host=" + host + " " + " ipNum=" + ipNum);
        Assertions.assertEquals(host, RequestUtils.getNumConvertIp(ipNum));
    }


    @Test
    void getControllerRequests() {
        List<RequestVo> list = AnnotationControllerUtils.getControllerRequests("org.ccs.opendfl.mysql");
        System.out.println(JSON.toJSON(list));
//        Assertions.assertTrue(list.size() > 0, "load size>00");
    }

}
