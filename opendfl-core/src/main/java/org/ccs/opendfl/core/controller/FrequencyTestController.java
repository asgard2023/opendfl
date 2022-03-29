package org.ccs.opendfl.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.limitcount.Frequency;
import org.ccs.opendfl.core.limitcount.Frequency2;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/frequencyTest")
@Slf4j
public class FrequencyTestController {

    @GetMapping("/serverTime")
    @ResponseBody
    public Object serverTime(HttpServletRequest request) {
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq")
    @ResponseBody
    @Frequency(time = 5, limit = 5, name = "serverTimeFreq")
    @Frequency2(time =3600, limit = 100, name = "serverTimeFreq")
    public Object serverTimeFreq(HttpServletRequest request){
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeUri")
    @ResponseBody
    public Object serverTimeUri(HttpServletRequest request){
        return System.currentTimeMillis();
    }

    @GetMapping("/waitLockTest")
    @ResponseBody
    @RequestLock(name = "waitLockTest", time=5, errMsg = "任务%s正在执行")
    public Object waitLockTest(@RequestParam(name="sleepTime", required = false) Integer sleepTime, HttpServletRequest request){
        if(sleepTime==null||sleepTime>100){
            sleepTime=1;
        }
        try {
            Thread.sleep(sleepTime*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
}
