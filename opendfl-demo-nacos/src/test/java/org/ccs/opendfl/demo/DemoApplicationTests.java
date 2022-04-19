package org.ccs.opendfl.demo;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
//@ActiveProfiles(value="dev")
class DemoApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(DemoApplicationTests.class);
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        System.out.println("-----black="+frequencyConfiguration.getBlack());
        logger.debug("--initLogCount={} ifActive={}", frequencyConfiguration.getInitLogCount(), frequencyConfiguration.getWhiteCodeUsers());
    }

    @Test
    public void redisTtl(){
        System.out.println("unexit key ttl="+redisTemplate.getExpire("testabcd"));
        System.out.println("foreraver key ttl="+redisTemplate.getExpire("test"));
    }



}
