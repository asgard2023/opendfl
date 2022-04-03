package org.ccs.opendfl.core;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;
import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value = "dev")
@Slf4j
public class FreqLimitChainTest {
    @Autowired
    private FreqLimitChain freqLimitChain;

    @BeforeEach
    public void init() {
        System.out.println("----init----");
        String freqTypeItems = "limit,userCount,userIp,ipUser,";
        freqLimitChain.sortStrategies(freqTypeItems);
    }

    @Test
    public void doCheckLimit_userIp() throws Exception {
        String lang = null;
        String ip = "192.168.5.2";
        String requestUri = "/frequencyTest/serverTimeFreqUserIp";
        String methodName = "serverTimeFreqUserIp";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, methodName, requestUri, curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setName("serverTimeFreqUserIp");
        frequencyVo.setLimit(1000);
        frequencyVo.setUserIpCount(10);

        String freqTypeItems = "limit,userCount,userIp,ipUser,";
        freqLimitChain.sortStrategies(freqTypeItems);
        Long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                strategyParamsVo = new RequestStrategyParamsVo(lang, ip + i, methodName, requestUri, curTime);
                strategyParamsVo.load(frequencyVo, "130");
                freqLimitChain.setStrategyParams(strategyParamsVo);
                freqLimitChain.clearLimit();
                this.freqLimitChain.doCheckLimit(freqLimitChain);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_userIp--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(successCount , 10, "successCount:" + successCount);
        Assertions.assertEquals(failCount , 10, "failCount:" + failCount);
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
    public void doCheckLimit_ipUser() throws Exception {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreqIpUser";
        String methodName = "serverTimeFreqIpUser";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, methodName, requestUri, curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(1000);
        frequencyVo.setIpUserCount(10);
        frequencyVo.setName("serverTimeFreqIpUser");

        String freqTypeItems = "limit,userCount,userIp,ipUser,";

        freqLimitChain.setStrategyParams(strategyParamsVo);
        freqLimitChain.sortStrategies(freqTypeItems);
        Long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "130" + i);
                freqLimitChain.clearLimit();
                this.freqLimitChain.doCheckLimit(freqLimitChain);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_ipUser--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(successCount , 10, "successCount:" + successCount);
        Assertions.assertEquals(failCount , 10, "failCount:" + failCount);
    }

    @Test
    public void doCheckLimit() throws Exception {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreq";
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, methodName, requestUri, curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(1000);

        String freqTypeItems = "limit,userCount,userIp,ipUser,";

        freqLimitChain.setStrategyParams(strategyParamsVo);
        freqLimitChain.sortStrategies(freqTypeItems);
        Long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 1020; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "131");
                freqLimitChain.clearLimit();
                this.freqLimitChain.doCheckLimit(freqLimitChain);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(successCount , 1000, "successCount:" + successCount);
        Assertions.assertEquals(failCount , 20, "failCount:" + failCount);

    }

    @Test
    public void doCheckLimit_limit() throws Exception {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreq";
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, methodName, requestUri, curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(1000);

        String freqTypeItems = "limit,";

        freqLimitChain.setStrategyParams(strategyParamsVo);
        freqLimitChain.sortStrategies(freqTypeItems);
        Long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 1020; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "132");
                freqLimitChain.clearLimit();
                this.freqLimitChain.doCheckLimit(freqLimitChain);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_limit--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(successCount , 1000, "successCount:" + successCount);
        Assertions.assertEquals(failCount , 20, "failCount:" + failCount);

    }

    @Test
    public void doCheckLimit_noLimit() throws Exception {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreq";
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, methodName, requestUri, curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(1000);

        String freqTypeItems = "userCount,userIp,ipUser,";

        freqLimitChain.setStrategyParams(strategyParamsVo);
        freqLimitChain.sortStrategies(freqTypeItems);
        Long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 1020; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "133");
                freqLimitChain.clearLimit();
                this.freqLimitChain.doCheckLimit(freqLimitChain);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_noLimit--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(successCount , 1020, "successCount:" + successCount);
        Assertions.assertEquals(failCount , 0, "failCount:" + failCount);

    }
}
