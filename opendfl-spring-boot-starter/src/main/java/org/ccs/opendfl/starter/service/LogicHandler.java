package org.ccs.opendfl.starter.service;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * do some logic
 *
 * @author chenjh
 * @date 2022-07-17
 */
@Slf4j
public class LogicHandler {
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    public void handle() {
        if (StringUtils.ifYes(frequencyConfiguration.getIfActive())) {
            log.info("---frequency--enable");
        }
    }
}
