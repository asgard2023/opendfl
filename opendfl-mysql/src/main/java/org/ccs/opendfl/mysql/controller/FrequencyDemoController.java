package org.ccs.opendfl.mysql.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chenjh
 */
@RestController
@RequestMapping("/frequencyDemo")
@Slf4j
public class FrequencyDemoController {
    @GetMapping("/serverTime")

    public Object serverTime(HttpServletRequest request) {
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq")

    @Frequency(limit = 5, name = "serverTimeFreq")
    public Object serverTimeFreq(HttpServletRequest request) {
        return System.currentTimeMillis();
    }


    @GetMapping("/waitTimeTest")

    @RequestLock(name = "waitTimeTest", time = 5, errMsg = "任务%s正在执行")
    public Object waitTimeTest(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        if (sleepTime == null || sleepTime > 100) {
            sleepTime = 1;
        }
        try {
            Thread.sleep(sleepTime * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
}
