package org.ccs.opendfl.core;


import com.alibaba.fastjson.JSONObject;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.vo.RequestTestVo;
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
    
    private final String HEADER_IP="x-forwarded-for";

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
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
        System.out.println("----serverTimeFreqDefaultNoUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeFreqNeedLogin() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "limit:needLogin";
        for (int i = 0; i < 2; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeNeedLogin")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqNeedLogin status=" + status + " content=" + content);
        }
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeNeedLogin")
                        .param("userId", "123")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();                 //得到返回代码
        String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
        Assertions.assertTrue(!content.contains(errorLimitType), "not fail");
    }

    /**
     * 用户访问频率-同一用户
     */
    @Test
    void serverTimeFreqSameUser() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String ip="192.168.9";
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            String userId = "123";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", userId)
                            .header(HEADER_IP, ip+i)
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
        System.out.println("----serverTimeFreqSameUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户
     */
    @Test
    void serverTimeFreqSameIpDiffUser() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String ip="192.168.9.100";
        String errorLimitType = "frequency:limitIp";
        for (int i = 0; i < 40; i++) {
            String userId = "123";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqLimitIp")
                            .param("userId", userId+i)
                            .header(HEADER_IP, ip)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqSameIpDiffUser status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqSameIpDiffUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertEquals(20, limtCount, "successCount:" + successCount);
        Assertions.assertEquals(20, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户同一资源ID限制
     */
    @Test
    void serverTimeFreq_resource_data() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String blackIp="192.168.0.1";
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            String userId = "1232";
            String dataId="abc2";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", userId)
                            .param("dataId", dataId)
                            .header(HEADER_IP, blackIp+i)
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
        System.out.println("----serverTimeFreq_resource_data  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertEquals(10, limtCount, "successCount:" + successCount);
        Assertions.assertEquals(10, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一IP同一资源ID限制
     */
    @Test
    void serverTimeFreq_resource_ip() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:limitIp";
        String blackIp="192.168.0.119";
        for (int i = 0; i < 20; i++) {
            String userId = "1234";
            String dataId="abcf";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", userId+i)
                            .param("dataId", dataId)
                            .header(HEADER_IP, blackIp)
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
        System.out.println("----serverTimeFreq_resource_ip  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertEquals(10, limtCount, "successCount:" + successCount);
        Assertions.assertEquals(10, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户
     */
    @Test
    void serverTimeJsonFreqSameUser() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        RequestTestVo requestTestVo;
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            String userId = "123";
            requestTestVo = new RequestTestVo();
            requestTestVo.setUserId(userId);
            String requestTestJson = JSONObject.toJSONString(requestTestVo);
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/frequencyTest/serverTimeJsonFreq")
//                            .param("userId", userId)
                            .content(requestTestJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeJsonFreqSameUser status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeJsonFreqSameUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    @Test
    void serverTimeStreamFreqSameUser() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        RequestTestVo requestTestVo;
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            String userId = "123";
            requestTestVo = new RequestTestVo();
            requestTestVo.setUserId(userId);
            String requestTestJson = JSONObject.toJSONString(requestTestVo);
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/frequencyTest/serverTimeStreamFreq")
//                            .param("userId", userId)
                            .content(requestTestJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeStreamFreqSameUser status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeStreamFreqSameUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-IP白名单
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
                            .header(HEADER_IP, whiteIp)
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
        System.out.println("----serverTimeFreqSameUser_whiteIp  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-用户白名单
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
        System.out.println("----serverTimeFreqSameUser_whiteUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-用户黑名单
     */
    @Test
    void serverTimeFreqSameUser_blackUser() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:black:user";
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
        System.out.println("----serverTimeFreqSameUser_blackUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-IP黑名单
     */
    @Test
    void serverTimeFreqSameUser_blackIp() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:black:ip";
        String blackIp = "192.168.5.103";
        for (int i = 0; i < 20; i++) {
            String userId = "124" + i;
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("userId", userId)
                            .header(HEADER_IP, blackIp)
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
        System.out.println("----serverTimeFreqSameUser_blackIp  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-不同用户
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
        System.out.println("----serverTimeFreqDiffUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    /**
     * ip限制
     */
    @Test
    void serverTimeFreqIp_ipUser() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitTypeIpUser = "frequency:ipUser";
        String ip="192.168.0.5";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqIp")
                            .param("userId", "126" + i)
                            .header(HEADER_IP, ip)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitTypeIpUser)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqIp  status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqIp  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertEquals(13, limtCount, "ipUserCount:" + limtCount);
        Assertions.assertEquals(7, successCount, "successCount:"+successCount);
    }

    /**
     * ip限制
     */
    @Test
    void serverTimeFreqIp_userIp() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitTypeUserIp = "frequency:userIp";
        String ip="192.168.0.2";
        String user="13user";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqIp")
                            .param("userId", user)
                            .header(HEADER_IP, ip+i)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitTypeUserIp)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqIp  status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqIp  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertEquals(13, limtCount, "ipUserCount:" + limtCount);
        Assertions.assertEquals(7, successCount, "successCount:"+successCount);
    }

    /**
     * 设备号黑名单限制
     */
    @Test
    void serverTimeDeviceIdByHader() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:black:device";
        for (int i = 0; i < 20; i++) {
            String userId = "123" + i;
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqDevice")
                            .param("userId", userId)
                            .header(RequestParams.DEVICE_ID, "blackDevice123")
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
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, limtCount, "limtCount:" + limtCount);
    }


    /**
     * 同IP多用户测试
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
        System.out.println("----serverTimeFreqIpUser  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "ipUserCount:" + limtCount);
    }

    /**
     * origin成功测试，即origin本身没问题
     */
    @Test
    void serverTimeFreq_whiteOrigin() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:white:origin";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqIpUser")
                            .param("userId", "123" + i)
                            .header(RequestParams.ORIGIN, "http://localhost:8080")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreq_whiteOrigin  status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreq_whiteOrigin  successCount=" + successCount + " limtCount=" + limtCount);
        //这个测试主要测origin,非origin异常，也算蓪过
        Assertions.assertEquals(20, successCount , "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "ipUserCount:" + limtCount);
    }

    /**
     * origin成功测试
     */
    @Test
    void serverTimeFreq_whiteOrigin_fail() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:white:origin";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqIpUser")
                            .param("userId", "123" + i)
                            .header(RequestParams.ORIGIN, "localhostFail")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreq_whiteOrigin_fail  status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreq_whiteOrigin_fail  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertEquals(0,successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, limtCount, "ipUserCount:" + limtCount);
    }


    /**
     * 同用户多IP测试
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
                            .header(HEADER_IP, ip)
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
        System.out.println("----serverTimeFreqUserIp  successCount=" + successCount + " limtCount=" + limtCount);
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "ipUserCount:" + limtCount);
    }

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
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/waitLockTestUser")
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
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/waitLockTestOrder")
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
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/waitLockTestOrderEtcdKv")
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
                    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/waitLockTestOrderZk")
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
