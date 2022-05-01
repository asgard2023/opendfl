package org.ccs.opendfl.core;

import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.utils.locktools.EtcdUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value="dev")
class ApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationTests.class);
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        logger.debug("--ifActive={} pwd={}", frequencyConfiguration.getIfActive(), frequencyConfiguration.getWhiteCodeUsers());
    }

    @Test
    public void redisTtl(){
        System.out.println("unexit key ttl="+redisTemplate.getExpire("testabcd"));
        System.out.println("foreraver key ttl="+redisTemplate.getExpire("test"));
    }

    @Test
    public void etcdTest() throws Exception {
        String v= EtcdUtil.getKV("/test/foo");
        System.out.println(v);
    }




}
