package org.ccs.opendfl.core;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.ReqSysType;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;
import org.ccs.opendfl.core.limitfrequency.FrequencyUtils;
import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitIpStrategy;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitIpUserStrategy;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitLimitStrategy;
import org.ccs.opendfl.core.strategy.limits.impl.FreqLimitUserIpStrategy;
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
class FreqLimitChainTest {
    @Autowired
    private FreqLimitChain freqLimitChain;
    private String deviceId = "freqTest123";

    @BeforeEach
    void init() {
        System.out.println("----init----");
        String freqTypeItems = "limit,limitIp,userIp,ipUser,";
        freqLimitChain.sortStrategies(freqTypeItems);
    }

    @Test
    void getRedisKeyBase() {
        FrequencyVo frequencyVo = new FrequencyVo();
        frequencyVo.setFreqLimitType(FreqLimitType.LIMIT_IP);
        frequencyVo.setTime(60);
        frequencyVo.setMethod("method");
        frequencyVo.setName("name");
        String key = FrequencyUtils.getRedisKeyBase(frequencyVo).toString();
        System.out.println(key);
    }

    @Test
    void getRedisKey() {
        FrequencyVo frequencyVo = new FrequencyVo();
        frequencyVo.setFreqLimitType(FreqLimitType.LIMIT_IP);
        frequencyVo.setTime(60);
        frequencyVo.setMethod("method");
        frequencyVo.setName("name");
        String ip="192.168.1.101";
        String key = FreqLimitIpStrategy.getRedisKey(frequencyVo, ip);
        System.out.println(key+" "+key.hashCode());

        key = FreqLimitIpUserStrategy.getRedisKey(frequencyVo, ip);
        System.out.println(key+" "+key.hashCode());

        key = FreqLimitLimitStrategy.getRedisKey(frequencyVo, "123", ip);
        System.out.println(key+" "+key.hashCode());

        key = FreqLimitUserIpStrategy.getRedisKey(frequencyVo, "123");
        System.out.println(key+" "+key.hashCode());
    }

    @Test
    void getRedisKey2() {
        FrequencyVo frequencyVo = new FrequencyVo();
        frequencyVo.setFreqLimitType(FreqLimitType.LIMIT_IP);
        frequencyVo.setTime(60);
        frequencyVo.setMethod("method");
        frequencyVo.setName("name");
        String ip="192.168.1.101";
        String key = FrequencyUtils.getRedisKey(frequencyVo, ip);
        System.out.println(key+" "+key.hashCode());

        frequencyVo.setFreqLimitType(FreqLimitType.IP_USER);
        key = FrequencyUtils.getRedisKey(frequencyVo, ip);
        System.out.println(key+" "+key.hashCode());

        frequencyVo.setFreqLimitType(FreqLimitType.USER_IP);
        key = FrequencyUtils.getRedisKey(frequencyVo, "123"+":"+ ip);
        System.out.println(key+" "+key.hashCode());

        frequencyVo.setFreqLimitType(FreqLimitType.LIMIT);
        key = FrequencyUtils.getRedisKey(frequencyVo, "123");
        System.out.println(key+" "+key.hashCode());
    }

    @Test
    void doCheckLimit_userIp() {
        String lang = null;
        String ip = "192.168.5.2";
        String requestUri = "/frequencyTest/serverTimeFreqUserIp";
        String methodName = "serverTimeFreqUserIp";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo;

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri, FreqLimitType.USER_IP, "serverTimeFreqUserIp", 30); ;
        frequencyVo.setLimit(10);

        String freqTypeItems = "limit,limitIp,userIp,ipUser,";
        freqLimitChain.sortStrategies(freqTypeItems);
        ReqSysType reqSysType = ReqSysType.PC;
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                strategyParamsVo = new RequestStrategyParamsVo(lang, ip + i, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);
                strategyParamsVo.load(frequencyVo, "130");
                strategyParamsVo.getChainOper().clearChain();
                this.freqLimitChain.doCheckLimit(freqLimitChain, strategyParamsVo);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_userIp--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(10, successCount, "successCount:" + successCount);
        Assertions.assertEquals(10, failCount, "failCount:" + failCount);
    }

    private FrequencyVo getFrequencyServerTime(String requestUri, FreqLimitType freqLimitType, String name, int time) {
        FrequencyVo frequencyVo = new FrequencyVo(name, time, freqLimitType, null, null, null);
        frequencyVo.setRequestUri(requestUri);
        frequencyVo.setErrMsg(ResultCode.USER_FREQUENCY_ERROR.getMsg());
        frequencyVo.setErrMsgEn(ResultCode.USER_FREQUENCY_ERROR.getMsg());
        return frequencyVo;
    }

    @Test
    void doCheckLimit_ipUser() {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreqIpUser";
        String methodName = "serverTimeFreqIpUser";
        ReqSysType reqSysType = ReqSysType.PC;
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri, FreqLimitType.IP_USER, "serverTimeFreqIpUser", 30);
        frequencyVo.setLimit(10);

        String freqTypeItems = "limit,limitIp,userIp,ipUser,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 20; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "130" + i);
                strategyParamsVo.getChainOper().clearChain();
                this.freqLimitChain.doCheckLimit(freqLimitChain, strategyParamsVo);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_ipUser--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(10, successCount, "successCount:" + successCount);
        Assertions.assertEquals(10, failCount, "failCount:" + failCount);
    }

    @Test
    void doCheckLimit() {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreq";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri, FreqLimitType.LIMIT, "serverTimeFreq", 30);
        frequencyVo.setLimit(1000);

        String freqTypeItems = "limit,limitIp,userIp,ipUser,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 1020; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "131");
                strategyParamsVo.getChainOper().clearChain();
                this.freqLimitChain.doCheckLimit(freqLimitChain, strategyParamsVo);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(1000, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, failCount, "failCount:" + failCount);

    }

    @Test
    void doCheckLimit_limit() {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreq";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri, FreqLimitType.LIMIT, "serverTimeFreq", 30);
        frequencyVo.setLimit(1000);

        String freqTypeItems = "limit,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 1020; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "132");
                strategyParamsVo.getChainOper().clearChain();
                this.freqLimitChain.doCheckLimit(freqLimitChain, strategyParamsVo);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_limit--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(1000, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, failCount, "failCount:" + failCount);

    }

    @Test
    void doCheckLimit_limit5() {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreq";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri, FreqLimitType.LIMIT, "serverTimeFreq", 30);
        frequencyVo.setLimit(3);

        String freqTypeItems = "limit,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 5; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "132");
                strategyParamsVo.getChainOper().clearChain();
                this.freqLimitChain.doCheckLimit(freqLimitChain, strategyParamsVo);
                successCount++;
                log.debug("-----i={} time={}", i, System.currentTimeMillis() - time);
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_limit--successCount={} failCount={} runTime={}/{}", successCount, failCount, System.currentTimeMillis() - time, System.currentTimeMillis() - curTime);
//        Assertions.assertEquals(1000, successCount, "successCount:" + successCount);
//        Assertions.assertEquals(20, failCount, "failCount:" + failCount);
    }

    @Test
    void doCheckLimit_noLimit() {
        String lang = null;
        String ip = "192.168.5.101";
        String requestUri = "/frequencyTest/serverTimeFreq";
        String methodName = "serverTimeFreq";
        ReqSysType reqSysType = ReqSysType.PC;
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri, FreqLimitType.LIMIT, "serverTimeFreq", 30);
        frequencyVo.setLimit(1000);

        String freqTypeItems = "limit,userIp,ipUser,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 1020; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "133");
                strategyParamsVo.getChainOper().clearChain();
                this.freqLimitChain.doCheckLimit(freqLimitChain, strategyParamsVo);
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_noLimit--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(1000, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, failCount, "failCount:" + failCount);

    }
}
