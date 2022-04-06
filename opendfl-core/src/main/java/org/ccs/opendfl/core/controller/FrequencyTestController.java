package org.ccs.opendfl.core.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitfrequency.Frequency2;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.core.vo.RequestTestVo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 频率限制支持
 *
 * @author chenjh
 */
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
    @Frequency2(time = 3600, limit = 100, name = "serverTimeFreq")
    public Object serverTimeFreq(HttpServletRequest request) {
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqIpUser")
    @ResponseBody
    @Frequency(time = 5, limit = 5, ipUserCount = 7, name = "serverTimeFreqIpUser")
    public Object serverTimeFreqIpUser(HttpServletRequest request) {
        log.info("----serverTimeFreqIpUser--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqUserIp")
    @ResponseBody
    @Frequency(time = 5, limit = 100, userIpCount = 7, name = "serverTimeFreqUserIp")
    public Object serverTimeFreqUserIp(HttpServletRequest request) {
        log.info("----serverTimeFreqUserIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqIp")
    @ResponseBody
    @Frequency(time = 5, limit = 5, userIpCount = 7, ipUserCount = 7, name = "serverTimeFreqIp")
    public Object serverTimeFreqIp(HttpServletRequest request) {
        log.info("----serverTimeFreqIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqDevice")
    @ResponseBody
    @Frequency(time = 5, limit = 5, name = "serverTimeFreqDevice")
    public Object serverTimeFreqDevice(HttpServletRequest request) {
        String deviceId = request.getParameter(RequestParams.DEVICE_ID);
        if(deviceId==null) {
            deviceId = request.getHeader(RequestParams.DEVICE_ID);
        }
        log.info("----serverTimeFreqDevice--deviceId={}", deviceId);
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeUri")
    @ResponseBody
    public Object serverTimeUri(HttpServletRequest request) {
        log.info("----serverTimeUri--account={}", request.getParameter("account"));
        return System.currentTimeMillis();
    }

    @PostMapping("/serverTimeJsonFreq")
    @ResponseBody
    @Frequency(time = 5, limit = 5, name = "serverTimeJsonFreq")
    public Object serverTimeJsonFreq(HttpServletRequest request, @RequestBody RequestTestVo requestTest) {
        ValidateUtils.notNull(requestTest.getUserId(), "userId is null");
        log.info("----serverTimeJsonFreq--userId={}", requestTest.getUserId());
        return System.currentTimeMillis();
    }

    @PostMapping("/serverTimeStreamFreq")
    @ResponseBody
    @Frequency(time = 5, limit = 5, name = "serverTimeStreamFreq")
    public Object serverTimeStreamFreq(HttpServletRequest request) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        br.close();
        String params = sb.toString();
        log.info("-----serverTimeStreamFreq--params={}", params);
        RequestTestVo requestTest = JSONObject.parseObject(params, RequestTestVo.class);
        ValidateUtils.notNull(requestTest.getUserId(), "userId is null");
        log.debug("----serverTimeStreamFreq--userId={}", requestTest.getUserId());
        return System.currentTimeMillis();
    }


    private Integer getSleepTimeIfNull(Integer sleepTime) {
        if (sleepTime == null || sleepTime > 100) {
            sleepTime = 1;
        }
        return sleepTime;
    }

    /**
     * 分布式锁测试，按用户，该用户前面请求未完成，再次请求全部拒绝
     *
     * @param sleepTime 测试线程 休睡时间(秒)
     * @return
     */
    @GetMapping("/waitLockTestUser")
    @ResponseBody
    @RequestLock(name = "waitLockTestUser", time = 5, errMsg = "任务%s正在执行")
    public Object waitLockTestUser(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        log.info("----waitLockTestUser--userId={} ip={}", request.getParameter(RequestParams.USER_ID), RequestUtils.getIpAddress(request));
        sleepTime = getSleepTimeIfNull(sleepTime);
        try {
            Thread.sleep(sleepTime * 1000L);
        } catch (InterruptedException e) {
            log.warn("---waitLockTestUser--Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new FailedException(e.getMessage());
        }
        return System.currentTimeMillis();
    }


    /**
     * 分布式锁测试，按用户，按roderId的分布式锁，只能一个请求能处理，其他全部拒绝
     *
     * @param sleepTime 测试线程 休睡时间(秒)
     * @return
     */
    @GetMapping("/waitLockTestOrder")
    @ResponseBody
    @RequestLock(name = "waitLockTestOrder", attrName = "orderId", time = 5, errMsg = "任务%s正在执行")
    public Object waitLockTestOrder(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        log.info("----waitLockTestOrder--userId={} orderId={} ip={}", request.getParameter(RequestParams.USER_ID), orderId, RequestUtils.getIpAddress(request));
        sleepTime = getSleepTimeIfNull(sleepTime);
        try {
            Thread.sleep(sleepTime * 1000L);
        } catch (InterruptedException e) {
            log.warn("---waitLockTestOrder--Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new FailedException(e.getMessage());
        }
        return System.currentTimeMillis();
    }
}
