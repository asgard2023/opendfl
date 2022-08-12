package org.ccs.opendfl.console;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles(value = "dev")
@AutoConfigureMockMvc
@WebAppConfiguration
public class FrequencyControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setupMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void serverTime() throws Exception{
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTime")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()) //期望结果是200
                .andReturn();
        System.out.println(mvcResult);
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
}
