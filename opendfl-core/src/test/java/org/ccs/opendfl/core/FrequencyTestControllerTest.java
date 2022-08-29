package org.ccs.opendfl.core;


import com.alibaba.fastjson.JSONObject;
import org.ccs.opendfl.core.filter.HttpServletRequestReplacedFilter;
import org.ccs.opendfl.core.utils.LangType;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(new HttpServletRequestReplacedFilter()).build();//建议使用这种
    }
    
    private final String HEADER_IP="x-forwarded-for";

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeFreq_dataId() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("dataId", "data"+i)
                            .param("userId", "123")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreq_dataId status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreq_dataId  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(10, successCount, "successCount:" + successCount);
        Assertions.assertEquals(10, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeFreq_userId() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                            .param("dataId", "data"+i)
                            .param("userId", "test1234")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreq_dataId status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreq_dataId  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(10, successCount, "successCount:" + successCount);
        Assertions.assertEquals(10, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeFreqAttr_dataId() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqAttr")
                            .param("dataId", "data"+i)
                            .param("userId", "1234")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqAttr_dataId status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqAttr_dataId  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount == 0, "limtCount:" + limtCount);
    }

    @Test
    void serverTimeFreqAttr_noDataId() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqAttr")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqAttr_noDataId status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqAttr_noDataId  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount == 20, "successCount:" + successCount);
        Assertions.assertTrue(limtCount  == 0, "limtCount:" + limtCount);
    }

    @Test
    void serverTimeFreqAttrCheck_noDataId() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqAttrCheck")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqAttrCheck_noDataId status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqAttrCheck_noDataId  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount == 20, "successCount:" + successCount);
        Assertions.assertTrue(limtCount == 0, "limtCount:" + limtCount);
    }

    @Test
    void serverTimeFreqAttrCheck_dataId() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqAttrCheck")
                            .param("dataId", "data"+i)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqAttrCheck_dataId status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqAttrCheck_dataId  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount == 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeFreqNeedLogin() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
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
        System.out.println("----serverTimeFreqNeedLogin  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
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

        long time = System.currentTimeMillis();
        int limtCount = 0;
        int successCount = 0;
        String ip="192.168.9.1";
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
        System.out.println("----serverTimeFreqSameUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户
     */
    @Test
    void serverTimeFreqSameIpDiffUser() throws Exception {
        long time = System.currentTimeMillis();
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
        System.out.println("----serverTimeFreqSameIpDiffUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(20, limtCount, "successCount:" + successCount);
        Assertions.assertEquals(20, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 同资源ID同IP访问次数限制
     */
    @Test
    void serverTimeFreqResIp() throws Exception {
        long time=System.currentTimeMillis();
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:resIp";
        String blackIp="192.168.0.1";
        for (int i = 0; i < 20; i++) {
            String userId = "1234";
            String dataId="abcf";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqResIp")
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
            System.out.println("----serverTimeFreqResIp status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqResIp  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(10, successCount, "successCount:" + successCount);
        Assertions.assertEquals(10, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 同资源ID同用户访问次数限制
     */
    @Test
    void serverTimeFreqResUser() throws Exception {
        long time=System.currentTimeMillis();
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:resUser";
        String blackIp="192.168.0.1";
        for (int i = 0; i < 20; i++) {
            String userId = "1234";
            String dataId="abcf";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqResUser")
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
            System.out.println("----serverTimeFreqResUser status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqResUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(10, successCount, "successCount:" + successCount);
        Assertions.assertEquals(10, limtCount, "limtCount:" + limtCount);
    }

    @Test
    void serverTimeFreqRes() throws Exception {
        long time=System.currentTimeMillis();
        int limtCountResUser = 0;
        int limtCountResIp = 0;
        int successCount = 0;
        String errorLimitTypeResUser = "frequency:resUser";
        String errorLimitTypeResIp = "frequency:resIp";
        String blackIp="192.168.0.1";
        for (int i = 0; i < 20; i++) {
            String userId = "1234";
            String dataId="abcf";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqRes")
                            .param("userId", userId)
                            .param("dataId", dataId)
                            .header(HEADER_IP, blackIp+i)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitTypeResUser)) {
                limtCountResUser++;
            }
            else if (content.contains(errorLimitTypeResIp)) {
                limtCountResIp++;
            }else {
                successCount++;
            }
            System.out.println("----serverTimeFreqRes status=" + status + " content=" + content);
        }

        for (int i = 0; i < 20; i++) {
            String userId = "1234";
            String dataId="abcf";
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqRes")
                            .param("userId", userId+i)
                            .param("dataId", dataId)
                            .header(HEADER_IP, blackIp)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitTypeResUser)) {
                limtCountResUser++;
            }
            else if (content.contains(errorLimitTypeResIp)) {
                limtCountResIp++;
            }else {
                successCount++;
            }
            System.out.println("----serverTimeFreqRes status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqRes  successCount=" + successCount + " limtCountResUser=" + limtCountResUser+" limtCountResIp="+limtCountResIp+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(20, successCount, "successCount:" + successCount);
        Assertions.assertEquals(10, limtCountResUser, "limtCountResUser:" + limtCountResUser);
        Assertions.assertEquals(10, limtCountResIp, "limtCountResIp:" + limtCountResIp);
    }

    /**
     * 用户访问频率-同一用户
     */
    @Test
    void serverTimeJsonFreqSameUser() throws Exception {
        long time=System.currentTimeMillis();
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
        System.out.println("----serverTimeJsonFreqSameUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    @Test
    void serverTimeStreamFreqSameUser() throws Exception {
        long time=System.currentTimeMillis();
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
        System.out.println("----serverTimeStreamFreqSameUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-IP白名单
     */
    @Test
    void serverTimeFreqSameUser_whiteIp() throws Exception {
        long time = System.currentTimeMillis();
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
        System.out.println("----serverTimeFreqSameUser_whiteIp  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-用户白名单
     */
    @Test
    void serverTimeFreqSameUser_whiteUser() throws Exception {
        long time = System.currentTimeMillis();
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
        System.out.println("----serverTimeFreqSameUser_whiteUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    @Test
    void serverTimeFreqSameUser_blackUser() throws Exception {
        serverTimeFreqSameUser_blackUser(null);
    }

    @Test
    void serverTimeFreqSameUser_blackUserCn() throws Exception {
        serverTimeFreqSameUser_blackUser(LangType.ZH.code);
    }

    @Test
    void serverTimeFreqSameUser_blackUserEn() throws Exception {
        serverTimeFreqSameUser_blackUser(LangType.EN.code);
    }

    /**
     * 用户访问频率-同一用户-用户黑名单
     */
    void serverTimeFreqSameUser_blackUser(String lang) throws Exception {
        long time=System.currentTimeMillis();
        int limtCount = 0;
        int successCount = 0;
        String errorLimitType = "frequency:black:user";
        String whiteUserId = "5103";
        for (int i = 0; i < 20; i++) {
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreq")
                    .param("userId", whiteUserId);
            if(lang!=null){
                builder=builder.header(RequestParams.LANG, lang);
            }
            MvcResult mvcResult = mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON)).andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqSameUser_blackUser status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqSameUser_blackUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-同一用户-IP黑名单
     */
    @Test
    void serverTimeFreqSameUser_blackIp() throws Exception {
        long time=System.currentTimeMillis();
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
        System.out.println("----serverTimeFreqSameUser_blackIp  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-不同用户
     */
    @Test
    void serverTimeFreqDiffUser() throws Exception {
        long time = System.currentTimeMillis();
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
        System.out.println("----serverTimeFreqDiffUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertEquals(0, limtCount, "limtCount:" + limtCount);
    }

    /**
     * ip限制
     */
    @Test
    void serverTimeFreqIp() throws Exception {
        long time = System.currentTimeMillis();
        int limtCountIpUser = 0;
        int limtCountUserIp = 0;
        int successCount = 0;
        String errorLimitTypeIpUser = "frequency:ipUser";
        String errorLimitTypeUserIp = "frequency:userIp";
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
                limtCountIpUser++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqIp  status=" + status + " content=" + content);
        }

        ip="192.168.0.5";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeFreqIp")
                            .param("userId", "126")
                            .header(HEADER_IP, ip+i)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitTypeUserIp)) {
                limtCountUserIp++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeFreqIp  status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeFreqIp  successCount=" + successCount + " limtCountIpUser="+limtCountIpUser+ " limtCountUserIp=" + limtCountUserIp+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(10, limtCountIpUser, "limtCountIpUser:" + limtCountIpUser);
        Assertions.assertEquals(10, limtCountUserIp, "limtCountUserIp:" + limtCountUserIp);
        Assertions.assertEquals(20, successCount, "successCount:"+successCount);
    }


    /**
     * 设备号黑名单限制
     */
    @Test
    void serverTimeDeviceIdByHader() throws Exception {
        long time = System.currentTimeMillis();
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
        System.out.println("----serverTimeDeviceIdByHader  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(0, successCount, "successCount:" + successCount);
        Assertions.assertEquals(20, limtCount, "limtCount:" + limtCount);
    }


    /**
     * 同IP多用户测试
     */
    @Test
    void serverTimeFreqIpUser() throws Exception {
        long time = System.currentTimeMillis();
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
        System.out.println("----serverTimeFreqIpUser  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertTrue(successCount > 0, "successCount:" + successCount);
        Assertions.assertTrue(limtCount > 0, "ipUserCount:" + limtCount);
    }

    /**
     * origin成功测试，即origin本身没问题
     */
    @Test
    void serverTimeFreq_whiteOrigin() throws Exception {
        long time = System.currentTimeMillis();
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
        System.out.println("----serverTimeFreq_whiteOrigin  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
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
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeUri() throws Exception {

        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeUri")
                            .param("account", "data"+i)
                            .param("userId", "123")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeUri status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeUri  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(8, successCount, "successCount:" + successCount);
        Assertions.assertEquals(12, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeUriPostGet_get() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeUriPostGet")
                            .param("account", "data"+i)
                            .param("userId", "123")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeUriPostGet_get status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeUriPostGet_get  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(8, successCount, "successCount:" + successCount);
        Assertions.assertEquals(12, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeUriPostGet_post() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/frequencyTest/serverTimeUriPostGet")
                            .param("account", "data"+i)
                            .param("userId", "123")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeUriPostGet_post status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeUriPostGet_post  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(8, successCount, "successCount:" + successCount);
        Assertions.assertEquals(12, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeUriPostGet2_get() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTimeUriPostGet2")
                            .param("account", "data"+i)
                            .param("userId", "123")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeUriPostGet_post status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeUriPostGet_post  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(8, successCount, "successCount:" + successCount);
        Assertions.assertEquals(12, limtCount, "limtCount:" + limtCount);
    }

    /**
     * 用户访问频率-没有用户（默认走IP）
     * user request limit-- no userId use IP as default
     */
    @Test
    void serverTimeUriPostGet2_post() throws Exception {
        int limtCount = 0;
        int successCount = 0;
        long time=System.currentTimeMillis();
        String errorLimitType = "frequency:limit";
        for (int i = 0; i < 20; i++) {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/frequencyTest/serverTimeUriPostGet2")
                            .param("account", "data"+i)
                            .param("userId", "123")
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            int status = mvcResult.getResponse().getStatus();                 //得到返回代码
            String content = mvcResult.getResponse().getContentAsString();    //得到返回结果
            if (content.contains(errorLimitType)) {
                limtCount++;
            } else {
                successCount++;
            }
            System.out.println("----serverTimeUriPostGet_post status=" + status + " content=" + content);
        }
        System.out.println("----serverTimeUriPostGet_post  successCount=" + successCount + " limtCount=" + limtCount+" time="+(System.currentTimeMillis()-time));
        Assertions.assertEquals(8, successCount, "successCount:" + successCount);
        Assertions.assertEquals(12, limtCount, "limtCount:" + limtCount);
    }
}
