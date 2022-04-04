package org.ccs.opendfl.core;

import org.ccs.opendfl.core.utils.RequestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestUtilsTest {
    @Test
    void isIpAddress() {
        String host;
        boolean isIpAddress;
        long time = System.currentTimeMillis();

        host = "192.168.0.101";
        isIpAddress = RequestUtils.isIpAddress(host);
        Assertions.assertTrue(isIpAddress, host + " ipv4 valid");
        System.out.println(System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        host = "fe80::ce24:2eff:fef2:75e8";
        isIpAddress = RequestUtils.isIpAddress(host);
        Assertions.assertTrue(isIpAddress, host + " ipv6 valid");
        System.out.println(System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        host = "2409:8a55:3312:c4e0:ce24:2eff:fef3:75e2";
        isIpAddress = RequestUtils.isIpAddress(host);
        Assertions.assertTrue(isIpAddress, host + " ipv6 valid");
        System.out.println(System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        host = "123.123.123.aa";
        isIpAddress = RequestUtils.isIpAddress(host);
        Assertions.assertFalse(isIpAddress, host + " invalid");
        System.out.println(System.currentTimeMillis() - time);

    }
}
