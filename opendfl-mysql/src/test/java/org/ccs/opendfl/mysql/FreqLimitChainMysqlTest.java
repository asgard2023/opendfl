package org.ccs.opendfl.mysql;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.ReqSysType;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultCode;
import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

/**
 * 不走controller，直接方法测试
 */
@SpringBootTest
@ActiveProfiles(value = "test")
@Slf4j
class FreqLimitChainMysqlTest {
    @Resource(name = "freqLimitChainMysql")
    private FreqLimitChain freqLimitChain;
    private String deviceId = "freqTest123";

    @BeforeEach
    void init() {
        System.out.println("----init----");
        String freqTypeItems = "limit,limitIp,userIp,ipUser,";
        freqLimitChain.sortStrategies(freqTypeItems);
    }

    @Test
    void doCheckLimit_userIp() {
        String lang = null;
        String ip = "192.168.5.2";
        String requestUri = "/frequencyTest/serverTimeFreqUserIp";
        String methodName = "serverTimeFreqUserIp";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo;

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setName("serverTimeFreqUserIp");
        frequencyVo.setLimit(1000);
        frequencyVo.setFreqLimitType(FreqLimitType.USER_IP_COUNT);

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
    void doCheckLimit_ipUser() {
        String lang = null;
        String ip = "192.168.5.101";
        ip= RequestUtils.convertIpv4(ip);
        String requestUri = "/frequencyTest/serverTimeFreqIpUser";
        String methodName = "serverTimeFreqIpUser";
        ReqSysType reqSysType = ReqSysType.PC;
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(1000);
        frequencyVo.setFreqLimitType(FreqLimitType.IP_USER_COUNT);
        frequencyVo.setName("serverTimeFreqIpUser");

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
    void doCheckLimit_all() {
        String lang = null;
        String ip = "192.168.5.101";
        ip= RequestUtils.convertIpv4(ip);
        String requestUri = "/frequencyTest/serverTimeFreq";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(100);

        String freqTypeItems = "limit,limitIp,userIp,ipUser,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 120; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "131");
                strategyParamsVo.getChainOper().clearChain();
                this.freqLimitChain.doCheckLimit(freqLimitChain, strategyParamsVo);
                if(!strategyParamsVo.getChainOper().isFail()) {
                    successCount++;
                }
                else{
                    failCount++;
                }
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(100, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, failCount, "failCount:" + failCount);

    }

    @Test
    void doCheckLimit_limit() {
        String lang = null;
        String ip = "192.168.5.101";
        ip= RequestUtils.convertIpv4(ip);
        String requestUri = "/frequencyTest/serverTimeFreq";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(100);
        frequencyVo.setTime(60);

        String freqTypeItems = "limit,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 120; i++) {
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
        Assertions.assertEquals(100, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, failCount, "failCount:" + failCount);

    }

    @Test
    void doCheckLimit_limitIp() {
        String lang = null;
        String ip = "192.168.5.109";
        ip= RequestUtils.convertIpv4(ip);
        String requestUri = "/frequencyTest/serverTimeFreq";
        ReqSysType reqSysType = ReqSysType.PC;
        String methodName = "serverTimeFreq";
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(10);

        String freqTypeItems = "limitIp,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 30; i++) {
            try {
                strategyParamsVo.load(frequencyVo, "132");
                strategyParamsVo.getChainOper().clearChain();
                this.freqLimitChain.doCheckLimit(freqLimitChain, strategyParamsVo);
                boolean isFail = strategyParamsVo.getChainOper().isFail();
                successCount++;
            } catch (BaseException e) {
                failCount++;
                log.error("-----count={} methodName={} title={} error={}", i, methodName, e.getTitle(), e.getMessage());
            }
        }
        log.info("-----doCheckLimit_limit--successCount={} failCount={} runTime={}", successCount, failCount, System.currentTimeMillis() - time);
        Assertions.assertEquals(20, successCount, "successCount:" + successCount);
        Assertions.assertEquals(10, failCount, "failCount:" + failCount);

    }


    @Test
    void doCheckLimit_noLimit() {
        String lang = null;
        String ip = "192.168.5.101";
        ip= RequestUtils.convertIpv4(ip);
        String requestUri = "/frequencyTest/serverTimeFreq";
        String methodName = "serverTimeFreq";
        ReqSysType reqSysType = ReqSysType.PC;
        Long curTime = System.currentTimeMillis();
        RequestStrategyParamsVo strategyParamsVo = new RequestStrategyParamsVo(lang, ip, deviceId, methodName, requestUri, reqSysType.getCode(), curTime);

        FrequencyVo frequencyVo = getFrequencyServerTime(requestUri);
        frequencyVo.setLimit(100);
        frequencyVo.setTime(60);

        String freqTypeItems = "userCount,userIp,ipUser,";

        freqLimitChain.sortStrategies(freqTypeItems);
        long time = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        for (int i = 0; i < 120; i++) {
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
        Assertions.assertEquals(120, successCount, "successCount:" + successCount);
        Assertions.assertEquals(0, failCount, "failCount:" + failCount);

    }
}
