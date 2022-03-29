package org.ccs.opendfl.console;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FrequencyControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void serverTime() throws Exception{
        ResultActions result=mockMvc.perform(MockMvcRequestBuilders.get("/frequencyTest/serverTime") // 发出请求
                        .contentType(MediaType.APPLICATION_JSON_UTF8) //期望的内容类型
                        .accept(MediaType.APPLICATION_JSON_UTF8)) //期望接收的内容类型
                .andExpect(MockMvcResultMatchers.status().isOk()) //期望结果是200
                .andDo(MockMvcResultHandlers.print()); // 期望把结果打印出来
    }
}
