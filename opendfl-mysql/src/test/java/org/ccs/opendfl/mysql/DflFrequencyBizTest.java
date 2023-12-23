package org.ccs.opendfl.mysql;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.mysql.dflcore.biz.IDflFrequencyBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@SpringBootTest
@ActiveProfiles(value = "dev")
@Slf4j
public class DflFrequencyBizTest {
    @Resource
    private IDflFrequencyBiz dflFrequencyBiz;
    @Test
    public void getFrequencyByCode(){
        this.dflFrequencyBiz.getFrequencyByCode_evict("serverTimeFreq", FreqLimitType.LIMIT.getType(), 5);
        DflFrequencyPo vo = this.dflFrequencyBiz.getFrequencyByCode("serverTimeFreq", FreqLimitType.LIMIT.getType(), 5);
        System.out.println(JSONUtil.toJsonStr(vo));
        vo = this.dflFrequencyBiz.getFrequencyByCode("serverTimeFreq", FreqLimitType.LIMIT.getType(), 5);
        System.out.println(JSONUtil.toJsonStr(vo));
    }
}
