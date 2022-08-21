package org.ccs.opendfl.locks;

import org.ccs.opendfl.core.filter.HttpServletRequestReplacedFilter;
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
 *分布式锁mock测试
 *
 * @author chenjh
 */
@SpringBootTest
@ActiveProfiles(value = "dev")
@AutoConfigureMockMvc
@WebAppConfiguration
public class LockTestControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(new HttpServletRequestReplacedFilter()).build();//建议使用这种
    }

    private final String HEADER_IP="x-forwarded-for";

    /**
     * 分布式锁-用户锁
     */
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
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/lockTest/waitLockTestUser")
                                    .param("userId", "123" + (iOper / 4))
                                    .param("sleepTime", "" + sleepTime)
                                    .header(HEADER_IP, ip)
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
        System.out.println("----waitLockTestUser  successCount=" + successCounter.get() + " limtCount=" + lockedCounter.get());
        Assertions.assertTrue(successCounter.get() >= 4, "successCount:" + successCounter.get());
        Assertions.assertTrue(lockedCounter.get() >= 15, "lockedCount:" + lockedCounter.get());
    }

    /**
     * 分布式气密-订单号
     */
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
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/lockTest/waitLockTestOrder")
                                    .param("userId", "123" + (iOper / 4))
                                    .param("sleepTime", "" + sleepTime)
                                    .param("orderId", orderId)
                                    .header(HEADER_IP, ip)
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
        System.out.println("----waitLockTestOrder  successCount=" + successCounter.get() + " limtCount=" + lockedCounter.get());
        Assertions.assertEquals(1, successCounter.get(), "successCount:" + successCounter.get());
        Assertions.assertEquals(size - 1, lockedCounter.get(), "lockedCount:" + lockedCounter.get());
    }

    /**
     * 分布式气密-订单号
     */
    @Test
    void waitLockTestOrderEtcdKv() {
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
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/lockTest/waitLockTestOrderEtcdKv")
                                    .param("userId", "123" + (iOper / 4))
                                    .param("sleepTime", "" + sleepTime)
                                    .param("orderId", orderId)
                                    .header(HEADER_IP, ip)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andReturn();
                    int status = mvcResult.getResponse().getStatus();                 //得到返回代码
                    String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
                    if (content.contains(errorLimitType)) {
                        lockedCounter.incrementAndGet();
                    } else {
                        successCounter.incrementAndGet();
                    }
                    System.out.println("----waitLockTestOrderEtcdKv  status=" + status + " content=" + content);
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
        System.out.println("----waitLockTestOrderEtcdKv  successCount=" + successCounter.get() + " limtCount=" + lockedCounter.get());
        Assertions.assertEquals(1, successCounter.get(), "successCount:" + successCounter.get());
        Assertions.assertEquals(size - 1, lockedCounter.get(), "lockedCount:" + lockedCounter.get());
    }

    /**
     * 分布式气密-订单号
     */
    @Test
    void waitLockTestOrderZk() {
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
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/lockTest/waitLockTestOrderZk")
                                    .param("userId", "123" + (iOper / 4))
                                    .param("sleepTime", "" + sleepTime)
                                    .param("orderId", orderId)
                                    .header(HEADER_IP, ip)
                                    .accept(MediaType.APPLICATION_JSON))
                            .andReturn();
                    int status = mvcResult.getResponse().getStatus();                 //得到返回代码
                    String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
                    if (content.contains(errorLimitType)) {
                        lockedCounter.incrementAndGet();
                    } else {
                        successCounter.incrementAndGet();
                    }
                    System.out.println("----waitLockTestOrderZk  status=" + status + " content=" + content);
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
        System.out.println("----waitLockTestOrderZk  successCount=" + successCounter.get() + " limtCount=" + lockedCounter.get());
        Assertions.assertEquals(1, successCounter.get(), "successCount:" + successCounter.get());
        Assertions.assertEquals(size - 1, lockedCounter.get(), "lockedCount:" + lockedCounter.get());
    }
}
