package org.ccs.opendfl.console;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 频率限制mock测试
 *
 * @author chenjh
 */
@SpringBootTest
@ActiveProfiles(value = "dev")
@AutoConfigureMockMvc
@WebAppConfiguration
class FrequencyTestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();//建议使用这种
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     *
     */
    @Test
    void serverTimeFreqDefaultNoUser() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqDefaultNoUser status=" + status + " content=" + content);
        }
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户
     *
     */
    @Test
    void serverTimeFreqSameUser() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            String userId = "123";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqSameUser status=" + status + " content=" + content);
        }
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-IP白名单
     *
     */
    @Test
    void serverTimeFreqSameUser_whiteIp() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:limit";
        String whiteIp = "192.168.5.101";
        for (int i = 0; i < 20; i++) {
            String userId = "123";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", userId)
                            .header("x-forwarded-for", whiteIp)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqSameUser_whiteIp status=" + status + " content=" + content);
        }
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-用户白名单
     *
     */
    @Test
    void serverTimeFreqSameUser_whiteUser() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:limit";
        String whiteUserId = "5101";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", whiteUserId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqSameUser_whiteUser status=" + status + " content=" + content);
        }
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-用户黑名单
     *
     */
    @Test
    void serverTimeFreqSameUser_blackUser() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:blackUser";
        String whiteUserId = "5103";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", whiteUserId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqSameUser_blackUser status=" + status + " content=" + content);
        }
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-IP黑名单
     *
     */
    @Test
    void serverTimeFreqSameUser_blackIp() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:blackIp";
        String blackIp = "192.168.5.103";
        for (int i = 0; i < 20; i++) {
            String userId = "124" + i;
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", userId)
                            .header("x-forwarded-for", blackIp)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqSameUser_blackIp status=" + status + " content=" + content);
        }
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-不同用户
     *
     */
    @Test
    void serverTimeFreqDiffUser() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            String userId = "123" + i;
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqDiffUser status=" + status + " content=" + content);
        }
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    /**
     * ip限制
     *
     */
    @Test
    void serverTimeFreqIp() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitTypeIpUser = "frequency:ipUser";
        String errorLimitTypeUserIp = "frequency:userIp";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqIp")
                            .param("userId", "125" + i)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitTypeIpUser) || content.contains(errorLimitTypeUserIp)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqIp  status=" + status + " content=" + content);
        }
        Assertions.assertTrue(limtCount > 0, "ipUserCount:" + limtCount);
//        Assertions.assertTrue("successCount:"+successCount,successCount>0);
    }

    /**
     * 同IP多用户测试
     *
     */
    @Test
    void serverTimeFreqIpUser() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:ipUser";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqIpUser")
                            .param("userId", "123" + i)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqIpUser  status=" + status + " content=" + content);
        }
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "ipUserCount:" + limtCount);
    }

    /**
     * 同用户多IP测试
     *
     */
    @Test
    void serverTimeFreqUserIp() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:userIp";
        String ip;
        for (int i = 0; i < 20; i++) {
            ip = "192.168.0." + i;
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqUserIp")
                            .param("userId", "123")
                            .header("x-forwarded-for", ip)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqUserIp  status=" + status + " content=" + content);
        }
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "ipUserCount:" + limtCount);
    }

    @Test
    void waitLockTestUser() {
        AtomicInteger lockedCounter = new AtomicInteger();
        AtomicInteger successCounter = new AtomicInteger();
        Integer sleepTime = 2;//单位秒(s)
        String errorLimitType = "frequency:lock";

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            final int iOper = i;
            final String ip = "192.168.0." + i;
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        this.waitLockTest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private void waitLockTest() throws Exception {
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/waitLockTestUser")
                                    .param("userId", "123" + (iOper / 4))
                                    .param("sleepTime", "" + sleepTime)
                                    .header("x-forwarded-for", ip)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andReturn();
                    int status = mvcResult.getResponse().getStatus();                 //得到返回代码
                    String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
                    if (content.contains(errorLimitType)) {
                        lockedCounter.incrementAndGet();
                    } else {
                        successCounter.incrementAndGet();
                    }
                    System.out.println("----waitLockTestUser  status=" + status + " content=" + content);
                }
            });
        }

        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                System.out.println("等待中");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        Assertions.assertTrue(successCounter.get() >= 4, "successCount:" + successCounter.get());
        Assertions.assertTrue(lockedCounter.get() >= 15, "lockedCount:" + lockedCounter.get());
    }

    @Test
    void waitLockTestOrder() {
        AtomicInteger lockedCounter = new AtomicInteger();
        AtomicInteger successCounter = new AtomicInteger();
        Integer sleepTime = 2;//单位秒(s)
        String errorLimitType = "frequency:lock";
        String orderId = "testOrder123";

        int size = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < size; i++) {
            final int iOper = i;
            final String ip = "192.168.0." + i;
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        this.waitLockTest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private void waitLockTest() throws Exception {
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/waitLockTestOrder")
                                    .param("userId", "123" + (iOper / 4))
                                    .param("sleepTime", "" + sleepTime)
                                    .param("orderId", orderId)
                                    .header("x-forwarded-for", ip)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andReturn();
                    int status = mvcResult.getResponse().getStatus();                 //得到返回代码
                    String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
                    if (content.contains(errorLimitType)) {
                        lockedCounter.incrementAndGet();
                    } else {
                        successCounter.incrementAndGet();
                    }
                    System.out.println("----waitLockTestOrder  status=" + status + " content=" + content);
                }
            });
        }

        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                System.out.println("等待中");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        Assertions.assertEquals(1, successCounter.get(), "successCount:" + successCounter.get());
        Assertions.assertEquals(size - 1, lockedCounter.get(), "lockedCount:" + lockedCounter.get());
    }
}
