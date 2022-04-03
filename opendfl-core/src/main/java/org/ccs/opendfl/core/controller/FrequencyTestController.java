package org.ccs.opendfl.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitfrequency.Frequency2;
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

    @GetMapping("/serverTimeFreqIpUser")
    @ResponseBody
    @Frequency(time = 5, limit = 5, ipUserCount = 7, name = "serverTimeFreqIpUser")
    public Object serverTimeFreqIpUser(HttpServletRequest request){
        System.out.println("----user="+request.getParameter("userId"));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqUserIp")
    @ResponseBody
    @Frequency(time = 5, limit = 100, userIpCount = 7, name = "serverTimeFreqUserIp")
    public Object serverTimeFreqUserIp(HttpServletRequest request){
        System.out.println("----user="+request.getParameter("userId"));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqIp")
    @ResponseBody
    @Frequency(time = 5, limit = 5, userIpCount = 7, ipUserCount = 7, name = "serverTimeFreqIp")
    public Object serverTimeFreqIp(HttpServletRequest request){
        System.out.println("----user="+request.getParameter("userId"));
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
            log.warn("---waitLockTest--Interrupted!", e);
        }
        return System.currentTimeMillis();
    }

    @GetMapping("/waitLockTest2")
    @ResponseBody
    @RequestLock(name = "waitLockTest2", time=5, errMsg = "任务%s正在执行")
    public Object waitLockTest2(@RequestParam(name="sleepTime", required = false) Integer sleepTime, HttpServletRequest request){
        if(sleepTime==null||sleepTime>100){
            sleepTime=1;
        }
        try {
            Thread.sleep(sleepTime*1000L);
        } catch (InterruptedException e) {
            log.warn("---waitLockTest--Interrupted!", e);
        }
        return System.currentTimeMillis();
    }
}
