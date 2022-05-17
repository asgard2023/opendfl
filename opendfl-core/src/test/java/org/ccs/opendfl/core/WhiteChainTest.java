package org.ccs.opendfl.core;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.ReqSysType;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;
import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 白名单测试
 *
 * @author chenjh
 */
@SpringBootTest
@ActiveProfiles(value = "dev")
@Slf4j
class WhiteChainTest {
    @Autowired
    private WhiteChain whiteChain;

    private String deviceId = "chainDeviceTest";


    @BeforeEach
    void init() {
        System.out.println("----init----");
        String freqTypeItems = "whiteUser,whiteIp,";
        whiteChain.sortStrategies(freqTypeItems);
    }

    private FrequencyVo getFrequencyServerTime(String requestUri) {
        FrequencyVo frequencyVo = new FrequencyVo();
        frequencyVo.setRequestUri(requestUri);
        frequencyVo.setName("serverTimeFreq");
        frequencyVo.setTime(5);
        frequencyVo.setErrMsg(ResultCode.USER_FREQUENCY_ERROR.getMsg());
        frequencyVo.setErrMsgEn(ResultCode.USER_FREQUENCY_ERROR.getMsg());
        return frequencyVo;
    }

    @Test
    void doCheckLimit_whiteIp_normal() {
        String lang = null;
        String ip = "192.168.5.105";
        ip = "" + RequestUtils.getIpConvertNum(ip);
        String requestUri = "/frequencyTest/serverTime";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTime";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo;

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setName("serverTime");
        frequencyVo.setLimit(1000);

        String freqTypeItems = "whiteIp,whiteUser,";
        whiteChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);
                strategyParamsVo.load(frequencyVo, "130" + i);
                this.whiteChain.doCheckLimit(whiteChain, strategyParamsVo);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_whiteIp_normal--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(20, successCount, "successCount:" + successCount);
        Assertions.assertEquals(0, failCount, "failCount:" + failCount);
    }


    @Test
    void doCheckLimit_whiteIp() {
        String lang = null;
        String ip = "192.168.5.101";
        ip = "" + RequestUtils.getIpConvertNum(ip);
        String requestUri = "/frequencyTest/serverTime";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTime";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo;

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setName("serverTime");
        frequencyVo.setLimit(1000);

        String freqTypeItems = "whiteIp,whiteUser,";
        whiteChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);
                strategyParamsVo.load(frequencyVo, "130" + i);
                this.whiteChain.doCheckLimit(whiteChain, strategyParamsVo);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_whiteIp--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(20, successCount, "successCount:" + successCount);
        Assertions.assertEquals(0, failCount, "failCount:" + failCount);
    }

    @Test
    void doCheckLimit_whiteUser() {
        String lang = null;
        String requestUri = "/frequencyTest/serverTime";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTime";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo;

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setName("serverTime");
        frequencyVo.setLimit(1000);

        String whiteUser = "5101";

        String freqTypeItems = "whiteIp,whiteUser,";
        whiteChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                String ip = "192.168.5.2" + i;
                ip = "" + RequestUtils.getIpConvertNum(ip);
                strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);
                strategyParamsVo.load(frequencyVo, whiteUser);
                this.whiteChain.doCheckLimit(whiteChain, strategyParamsVo);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_whiteUser--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(20, successCount, "successCount:" + successCount);
        Assertions.assertEquals(0, failCount, "failCount:" + failCount);
    }

}
