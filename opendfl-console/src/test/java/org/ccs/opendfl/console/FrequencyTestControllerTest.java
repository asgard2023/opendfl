package org.ccs.opendfl.console;

import junit.framework.TestCase;
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
    public void setUp() throws Exception {
//        MockMvcBuilders.webAppContextSetup(WebApplicationContext webApplicationContext)//指定WebApplicationContext，将会从该上下文获取相应的控制器并得到相应的MockMvc；
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();//建议使用这种
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     *
     * @throws Exception
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
        TestCase.assertTrue("successCount:" + successCount, successCount > 0);
        TestCase.assertTrue("limtCount:" + limtCount, limtCount > 0);
    }

    /**
     * 用户访问频率-同一用户
     *
     * @throws Exception
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
        TestCase.assertTrue("successCount:" + successCount, successCount > 0);
        TestCase.assertTrue("limtCount:" + limtCount, limtCount > 0);
    }

    /**
     * 用户访问频率-同一用户-IP白名单
     *
     * @throws Exception
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
        TestCase.assertTrue("successCount:" + successCount, successCount > 0);
        TestCase.assertEquals("limtCount:" + limtCount , 0, limtCount);
    }

    /**
     * 用户访问频率-同一用户-用户白名单
     *
     * @throws Exception
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
        TestCase.assertTrue("successCount:" + successCount, successCount > 0);
        TestCase.assertEquals("limtCount:" + limtCount , 0, limtCount);
    }

    /**
     * 用户访问频率-同一用户-用户黑名单
     *
     * @throws Exception
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
        TestCase.assertEquals("successCount:" + successCount , 0, successCount);
        TestCase.assertTrue("limtCount:" + limtCount, limtCount > 0);
    }

    /**
     * 用户访问频率-同一用户-IP黑名单
     *
     * @throws Exception
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
        TestCase.assertEquals("successCount:" + successCount , 0, successCount);
        TestCase.assertTrue("limtCount:" + limtCount, limtCount > 0);
    }

    /**
     * 用户访问频率-不同用户
     *
     * @throws Exception
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
        TestCase.assertTrue("successCount:" + successCount, successCount > 0);
        TestCase.assertEquals("limtCount:" + limtCount , 0, limtCount);
    }

    /**
     * ip限制
     *
     * @throws Exception
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
        TestCase.assertTrue("ipUserCount:" + limtCount, limtCount > 0);
//        TestCase.assertTrue("successCount:"+successCount,successCount>0);
    }

    /**
     * 同IP多用户测试
     *
     * @throws Exception
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
        TestCase.assertTrue("successCount:" + successCount, successCount > 0);
        TestCase.assertTrue("ipUserCount:" + limtCount, limtCount > 0);
    }

    /**
     * 同用户多IP测试
     *
     * @throws Exception
     */
    @Test
    void serverTimeFreqUserIp() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:userIp";
        String ip = "192.168.0.";
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
        TestCase.assertTrue("successCount:" + successCount, successCount > 0);
        TestCase.assertTrue("ipUserCount:" + limtCount, limtCount > 0);
    }
}
