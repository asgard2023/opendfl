package org.ccs.opendfl.mysql;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value="dev")
class MysqlApplicationTests {
    private static Logger logger = LoggerFactory.getLogger(MysqlApplicationTests.class);
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        logger.debug("--initLogCount={} ifActive={}", frequencyConfiguration.getInitLogCount(), frequencyConfiguration.getWhiteCodeUsers());
    }

    @Test
    public void redisTtl(){
        System.out.println("unexit key ttl="+redisTemplate.getExpire("testabcd"));
        System.out.println("foreraver key ttl="+redisTemplate.getExpire("test"));
    }



}
