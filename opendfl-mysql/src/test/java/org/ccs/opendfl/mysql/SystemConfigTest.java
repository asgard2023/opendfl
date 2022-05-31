package org.ccs.opendfl.mysql;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.mysql.dflsystem.constant.SystemConfigCodes;
import org.ccs.opendfl.mysql.dflsystem.utils.SystemConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value = "dev")
@Slf4j
public class SystemConfigTest {
    @Test
    void getByCache(){
        Integer outLimitMinTime= SystemConfig.getByCache(SystemConfigCodes.LIMIT_OUT_LIMIT_MIN_TIME, SystemConfigCodes.PARENT_ID_FREQUENCY);
        System.out.println(outLimitMinTime);
        Integer minRunTime= SystemConfig.getByCache(SystemConfigCodes.FREQUENCY_MIN_RUN_TIME, SystemConfigCodes.PARENT_ID_FREQUENCY);
        System.out.println(minRunTime);
        Integer logDebugCount= SystemConfig.getByCache(SystemConfigCodes.FREQUENCY_INIT_LOG_DEBUG_COUNT, SystemConfigCodes.PARENT_ID_FREQUENCY);
        System.out.println("logDebugCount="+logDebugCount);
        Integer lockIfActive= SystemConfig.getByCache(SystemConfigCodes.LOCK_IF_ACTIVE, SystemConfigCodes.PARENT_ID_LOCK);
        System.out.println("lockIfActive="+lockIfActive);
    }

    @Test
    void getByLang(){
        Integer outLimitMinTime= SystemConfig.getByLang(SystemConfigCodes.LIMIT_OUT_LIMIT_MIN_TIME.getCode());
        System.out.println(outLimitMinTime++);
    }
}
