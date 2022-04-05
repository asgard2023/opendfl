package org.ccs.opendfl.core;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;
import org.ccs.opendfl.core.strategy.black.BlackChain;
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
 * 黑名单测试
 *
 * @author chenjh
 */
@SpringBootTest
@ActiveProfiles(value = "dev")
@Slf4j
class BlackChainTest {
    @Autowired
    private BlackChain blackChain;

    @BeforeEach
    void init() {
        System.out.println("----init----");
        String freqTypeItems = "blackUser,blackIp,";
        blackChain.sortStrategies(freqTypeItems);
    }

    private FrequencyVo getFrequencyServerTime(String requestUri) {
        FrequencyVo frequencyVo = new FrequencyVo();
        frequencyVo.setRequestUri(requestUri);
        frequencyVo.setName("serverTimeFreq");
        frequencyVo.setTime(5);
        frequencyVo.setErrMsg(ResultCode.USER_FREQUENCY_ERROR_MSG);
        frequencyVo.setErrMsgEn(ResultCode.USER_FREQUENCY_ERROR_MSG);
        return frequencyVo;
    }

    @Test
    void doCheckLimit_blackIp_normal() {
        String lang = null;
        String ip = "192.168.5.105";
        String requestUri = "/frequencyTest/serverTime";
        String methodName = "serverTime";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo;

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setName("serverTime");
        frequencyVo.setLimit(1000);

        String freqTypeItems = "blackIp,blackUser,";
        blackChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                strategyParamsVo = new RequestStrategyParamsVo(lang, ip, methodName, requestUri, curTime);
                strategyParamsVo.load(frequencyVo, "130" + i);
                blackChain.setStrategyParams(strategyParamsVo);
                blackChain.clearLimit();
                boolean isBlack = this.blackChain.doCheckLimit(blackChain);
                if (isBlack) {
                    failCount++;
                } else {
                    successCount++;
                }
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_blackIp_normal--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(20, successCount, "successCount:" + successCount);
        Assertions.assertEquals(0, failCount, "failCount:" + failCount);
    }


    @Test
    void doCheckLimit_blackIp() {
        String lang = null;
        String ip = "192.168.5.103";
        ip= ""+RequestUtils.getIpConvertNum(ip);
        String requestUri = "/frequencyTest/serverTime";
        String methodName = "serverTime";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo;

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setName("serverTime");
        frequencyVo.setLimit(1000);

        String freqTypeItems = "blackIp,blackUser,";
        blackChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                strategyParamsVo = new RequestStrategyParamsVo(lang, ip, methodName, requestUri, curTime);
                strategyParamsVo.load(frequencyVo, "130" + i);
                blackChain.setStrategyParams(strategyParamsVo);
                blackChain.clearLimit();
                boolean isBlack = this.blackChain.doCheckLimit(blackChain);
                if (isBlack) {
                    failCount++;
                } else {
                    successCount++;
                }
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_blackIp--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, failCount, "failCount:" + failCount);
    }

    @Test
    void doCheckLimit_blackUser() {
        String lang = null;
        String requestUri = "/frequencyTest/serverTime";
        String methodName = "serverTime";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo;

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setName("serverTime");
        frequencyVo.setLimit(1000);

        String blackUser = "5103";

        String freqTypeItems = "blackIp,blackUser,";
        blackChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                String ip = "192.168.5.2" + i;
                ip= ""+RequestUtils.getIpConvertNum(ip);
                strategyParamsVo = new RequestStrategyParamsVo(lang, ip, methodName, requestUri, curTime);
                strategyParamsVo.load(frequencyVo, blackUser);
                blackChain.setStrategyParams(strategyParamsVo);
                blackChain.clearLimit();
                boolean isBlack = this.blackChain.doCheckLimit(blackChain);
                if (isBlack) {
                    failCount++;
                } else {
                    successCount++;
                }
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_blackUser--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, failCount, "failCount:" + failCount);
    }

}
