package org.ccs.opendfl.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chenjh
 */
@RestController
@RequestMapping("/frequencyDemo")
@Slf4j
public class FrequencyDemoController {
    @GetMapping("/serverTime")
    @ResponseBody
    public Object serverTime(HttpServletRequest request) {
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq")
    @ResponseBody
    @Frequency(limit = 5, name = "serverTimeFreq")
    public Object serverTimeFreq(HttpServletRequest request) {
        return System.currentTimeMillis();
    }


    @GetMapping("/waitTimeTest")
    @ResponseBody
    @RequestLock(name = "waitTimeTest", time = 5, errMsg = "任务%s正在执行")
    public Object waitTimeTest(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        int maxSecond = 100;
        if (sleepTime == null || sleepTime < 0 || sleepTime > maxSecond) {
            sleepTime = 1;
        }
        try {
            Thread.sleep(sleepTime * FrequencyConstant.TIME_MILLISECOND_TO_SECOND);
        } catch (InterruptedException e) {
            log.warn("---waitLockTestUser--Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new FailedException(e.getMessage());
        }
        return System.currentTimeMillis();
    }
}
