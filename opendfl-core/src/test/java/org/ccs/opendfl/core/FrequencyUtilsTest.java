package org.ccs.opendfl.core;

import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FrequencyUtilsTest {
    @Test
    void getErrMsg() {
        String msg = "每秒#{limit}次";
        String msgResult = FrequencyUtils.getErrMsg(msg, 60, 10);
        System.out.println(msg + "---" + msgResult);
        Assertions.assertEquals("每秒10次", msgResult);

        msg = "每#{time}秒#{limit}次";
        msgResult = FrequencyUtils.getErrMsg(msg, 60, 10);
        System.out.println(msg + "---" + msgResult);
        Assertions.assertEquals("每60秒10次", msgResult);

        msg = "频率超限";
        msgResult = FrequencyUtils.getErrMsg(msg, 60, 10);
        System.out.println(msg + "---" + msgResult);
        Assertions.assertEquals(msg, msgResult);
    }
}
