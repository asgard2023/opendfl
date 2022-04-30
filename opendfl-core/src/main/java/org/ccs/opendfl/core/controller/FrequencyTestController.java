package org.ccs.opendfl.core.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IMaxRunTimeBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.ReqLockType;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitfrequency.Frequency2;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.core.vo.MaxRunTimeVo;
import org.ccs.opendfl.core.vo.RequestTestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 频率限制支持
 *
 * @author chenjh
 */
@RestController
@RequestMapping("/frequencyTest")
@Slf4j
public class FrequencyTestController {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Autowired
    private IMaxRunTimeBiz maxRunTimeBiz;
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    /**
     * 找出second时间内执行最慢的近count个接口
     * 可用于做监控告警，比如5秒内执行时间最长的接口
     *
     * @param request HttpServletRequest
     * @param second  单位秒
     * @param count   接口个数
     * @return
     */
    @GetMapping("/monitor")
    @ResponseBody
    public ResultData monitor(HttpServletRequest request, @RequestParam(name = "second", defaultValue = "30") Integer second
            , @RequestParam(name = "count", defaultValue = "20") Integer count) {
        log.info("----monitor--userId={}", request.getParameter(RequestParams.USER_ID));
        if (!StringUtils.ifYes(frequencyConfiguration.getRunTimeMonitor())) {
            throw new FailedException("feature is not open, see frequency.runTimeMonitor");
        }
        List<MaxRunTimeVo> list = this.maxRunTimeBiz.getNewlyMaxRunTime(second, count);
        return ResultData.success(list);
    }

    @GetMapping("/serverTime")
    @ResponseBody
    public Object serverTime(HttpServletRequest request) {
        log.info("----serverTime--userId={} {}", request.getParameter(RequestParams.USER_ID), redisHost);
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq")
    @ResponseBody
    @Frequency(time = 5, limit = 5, name = "serverTimeFreq")
    @Frequency2(time = 3600, limit = 100, name = "serverTimeFreq")
    public Object serverTimeFreq(HttpServletRequest request) {
        log.info("----serverTimeFreq--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeNeedLogin")
    @ResponseBody
    @Frequency(time = 5, limit = 5, needLogin = true, name = "serverTimeNeedLogin")
    @Frequency2(time = 3600, needLogin = true, limit = 100, name = "serverTimeNeedLogin")
    public Object serverTimeNeedLogin(HttpServletRequest request) {
        log.info("----serverTimeNeedLogin--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq120")
    @ResponseBody
    @Frequency(time = 120, limit = 5, name = "serverTimeFreq120")
    @Frequency2(time = 3600, limit = 100, name = "serverTimeFreq120")
    public Object serverTimeFreq120(HttpServletRequest request) {
        log.info("----serverTimeFreq120--userId={}", request.getParameter(RequestParams.USER_ID));
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
    public Object serverTimeFreqDevice(HttpServletRequest request) {
        String deviceId = request.getParameter(RequestParams.DEVICE_ID);
        if (deviceId == null) {
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

    /**
     * json参数测试
     *
     * @param requestTest RequestTestVo
     * @return current time
     */
    @PostMapping("/serverTimeJsonFreq")
    @ResponseBody
    @Frequency(time = 5, limit = 5, name = "serverTimeJsonFreq")
    public Object serverTimeJsonFreq(@RequestBody RequestTestVo requestTest) {
        ValidateUtils.notNull(requestTest.getUserId(), "userId is null");
        log.info("----serverTimeJsonFreq--userId={}", requestTest.getUserId());
        return System.currentTimeMillis();
    }

    /**
     * 字符流测试
     *
     * @param request HttpServletRequest
     * @return current time
     */
    @PostMapping("/serverTimeStreamFreq")
    @ResponseBody
    @Frequency(time = 5, limit = 5, name = "serverTimeStreamFreq")
    public Object serverTimeStreamFreq(HttpServletRequest request) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        br.close();
        String params = sb.toString();
        log.info("-----serverTimeStreamFreq--params={}", params);
        RequestTestVo requestTest = JSON.parseObject(params, RequestTestVo.class);
        ValidateUtils.notNull(requestTest.getUserId(), "userId is null");
        log.debug("----serverTimeStreamFreq--userId={}", requestTest.getUserId());
        return System.currentTimeMillis();
    }


    private Integer getSleepTimeIfNull(Integer sleepTime) {
        int maxSleepTime = 100;
        if (sleepTime == null || sleepTime > maxSleepTime) {
            sleepTime = 1;
        }
        return sleepTime;
    }

    /**
     * 分布式锁测试，按用户，该用户前面请求未完成，再次请求全部拒绝
     *
     * @param sleepTime 测试线程 休睡时间(秒)
     * @return currentTime
     */
    @GetMapping("/waitLockTestUser")
    @ResponseBody
    @RequestLock(name = "waitLockTestUser", time = 5, errMsg = "任务%s正在执行")
    public Object waitLockTestUser(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        log.info("----waitLockTestUser--userId={} ip={}", request.getParameter(RequestParams.USER_ID), RequestUtils.getIpAddress(request));
        sleepTime = getSleepTimeIfNull(sleepTime);
        try {
            Thread.sleep(sleepTime * FrequencyConstant.TIME_MILLISECOND_TO_SECOND);
        } catch (InterruptedException e) {
            log.warn("---waitLockTestUser--Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new FailedException(e.getMessage());
        }
        return System.currentTimeMillis();
    }


    /**
     * 分布式锁测试，按用户，按oderId的分布式锁，只能一个请求能处理，其他全部拒绝
     *
     * @param sleepTime 测试线程 休睡时间(秒)
     * @return currentTime
     */
    @GetMapping("/waitLockTestOrder")
    @ResponseBody
    @RequestLock(name = "waitLockTestOrder", attrName = "orderId", time = 5, errMsg = "任务%s正在执行")
    public Object waitLockTestOrder(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        log.info("----waitLockTestOrder--userId={} orderId={} ip={}", request.getParameter(RequestParams.USER_ID), orderId, RequestUtils.getIpAddress(request));
        sleepTime = getSleepTimeIfNull(sleepTime);
        try {
            Thread.sleep(sleepTime * FrequencyConstant.TIME_MILLISECOND_TO_SECOND);
        } catch (InterruptedException e) {
            log.warn("---waitLockTestOrder--Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new FailedException(e.getMessage());
        }
        return System.currentTimeMillis();
    }

    @GetMapping("/waitLockTestOrderEtcd")
    @ResponseBody
    @RequestLock(name = "waitLockTestOrderEtcd", attrName = "orderId", time = 5, errMsg = "任务%s正在执行", lockType = ReqLockType.ETCD)
    public Object waitLockTestOrderEtcd(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        log.info("----waitLockTestOrderEtcd--userId={} orderId={} ip={}", request.getParameter(RequestParams.USER_ID), orderId, RequestUtils.getIpAddress(request));
        sleepTime = getSleepTimeIfNull(sleepTime);
        try {
            Thread.sleep(sleepTime * FrequencyConstant.TIME_MILLISECOND_TO_SECOND);
        } catch (InterruptedException e) {
            log.warn("---waitLockTestOrderEtcd--Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new FailedException(e.getMessage());
        }
        return System.currentTimeMillis();
    }

    @GetMapping("/waitLockTestOrderEtcdSync")
    @ResponseBody
    @RequestLock(name = "waitLockTestOrderEtcdSync", attrName = "orderId", time = 5, errMsg = "任务%s正在执行", lockType = ReqLockType.ETCD_SYNC)
    public Object waitLockTestOrderEtcdSync(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        log.info("----waitLockTestOrderEtcdSync--userId={} orderId={} ip={}", request.getParameter(RequestParams.USER_ID), orderId, RequestUtils.getIpAddress(request));
        sleepTime = getSleepTimeIfNull(sleepTime);
        try {
            Thread.sleep(sleepTime * FrequencyConstant.TIME_MILLISECOND_TO_SECOND);
        } catch (InterruptedException e) {
            log.warn("---waitLockTestOrderEtcdSync--Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new FailedException(e.getMessage());
        }
        return System.currentTimeMillis();
    }
}
