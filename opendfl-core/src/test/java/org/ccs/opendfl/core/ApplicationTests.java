package org.ccs.opendfl.core;

import org.ccs.opendfl.core.config.ConsoleConfiguration;
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
    private static Logger logger = LoggerFactory.getLogger(ApplicationTests.class);
    @Autowired
    private ConsoleConfiguration consoleConfiguration;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        logger.debug("--ifConsole={} pwd={}", consoleConfiguration.getIfConsole(), consoleConfiguration.getConsoleWhiteIp());
    }

    @Test
    public void redisTtl(){
        System.out.println("unexit key ttl="+redisTemplate.getExpire("testabcd"));
        System.out.println("foreraver key ttl="+redisTemplate.getExpire("test"));
    }



}
