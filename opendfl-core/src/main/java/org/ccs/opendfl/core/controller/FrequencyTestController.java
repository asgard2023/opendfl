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
import org.ccs.opendfl.core.limitfrequency.Frequency3;
import org.ccs.opendfl.core.limitfrequency.Frequency4;
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
    public Object serverTime(HttpServletRequest request) {
        log.info("----serverTime--userId={} {}", request.getParameter(RequestParams.USER_ID), redisHost);
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq")
    @Frequency(time = 5, limit = 5, name = "serverTimeFreq")
    @Frequency2(time = 3600, limit = 100, name = "serverTimeFreq")
    @Frequency3(time = 60, limit = 10, resource = true, attrName = "dataId", name = "serverTimeFreqRes")
    @Frequency4(time = 60, limit = 10, resource = true, attrName = "dataId", ipUserCount = 10, name = "serverTimeFreqIp")
    public Object serverTimeFreq(HttpServletRequest request) {
        log.info("----serverTimeFreq--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeNeedLogin")
    @Frequency(time = 5, limit = 5, needLogin = true, name = "serverTimeNeedLogin")
    @Frequency2(time = 3600, needLogin = true, limit = 100, name = "serverTimeNeedLogin")
    public Object serverTimeNeedLogin(HttpServletRequest request) {
        log.info("----serverTimeNeedLogin--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq120")
    @Frequency(time = 120, limit = 5, name = "serverTimeFreq120")
    @Frequency2(time = 3600, limit = 100, name = "serverTimeFreq120")
    public Object serverTimeFreq120(HttpServletRequest request) {
        log.info("----serverTimeFreq120--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqIpUser")
    @Frequency(time = 5, limit = 5, ipUserCount = 7, name = "serverTimeFreqIpUser")
    public Object serverTimeFreqIpUser(HttpServletRequest request) {
        log.info("----serverTimeFreqIpUser--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqUserIp")
    @Frequency(time = 5, limit = 100, userIpCount = 7, name = "serverTimeFreqUserIp")
    public Object serverTimeFreqUserIp(HttpServletRequest request) {
        log.info("----serverTimeFreqUserIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqIp")
    @Frequency(time = 30, limit = 100, userIpCount = 7, ipUserCount = 7, name = "serverTimeFreqIp")
    public Object serverTimeFreqIp(HttpServletRequest request) {
        log.info("----serverTimeFreqIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqDevice")
    public Object serverTimeFreqDevice(HttpServletRequest request) {
        String deviceId = request.getParameter(RequestParams.DEVICE_ID);
        if (deviceId == null) {
            deviceId = request.getHeader(RequestParams.DEVICE_ID);
        }
        log.info("----serverTimeFreqDevice--deviceId={}", deviceId);
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeUri")
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
        return getSleepTimeIfNull(sleepTime, 1);
    }
    private Integer getSleepTimeIfNull(Integer sleepTime, Integer defaultValue) {
        int maxSleepTime = 100;
        if (sleepTime == null || sleepTime > maxSleepTime) {
            sleepTime = defaultValue;
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
    @RequestLock(name = "waitLockTestUser", time = 5, errMsg = "任务%s正在执行")
    public Object waitLockTestUser(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        log.info("----waitLockTestUser--userId={} ip={}", request.getParameter(RequestParams.USER_ID), RequestUtils.getIpAddress(request));
        sleepByTime(sleepTime);
        return System.currentTimeMillis();
    }


    /**
     * 分布式锁测试，按用户，按oderId的分布式锁，只能一个请求能处理，其他全部拒绝
     *
     * @param sleepTime 测试线程 休睡时间(秒)
     * @return currentTime
     */
    @GetMapping("/waitLockTestOrder")
    @RequestLock(name = "waitLockTestOrder", attrName = "orderId", time = 5, errMsg = "任务%s正在执行")
    public Object waitLockTestOrder(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        log.info("----waitLockTestOrder--userId={} orderId={} ip={}", request.getParameter(RequestParams.USER_ID), orderId, RequestUtils.getIpAddress(request));
        sleepByTime(sleepTime);
        return System.currentTimeMillis();
    }

    @GetMapping("/waitLockTestOrderEtcdKv")
    @RequestLock(name = "waitLockTestOrderEtcdKv", attrName = "orderId", time = 5, errMsg = "任务%s正在执行", lockType = ReqLockType.ETCD_KV)
    public Object waitLockTestOrderEtcdKv(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        log.info("----waitLockTestOrderEtcdKv--userId={} orderId={} ip={}", request.getParameter(RequestParams.USER_ID), orderId, RequestUtils.getIpAddress(request));
        sleepByTime(sleepTime);
        return System.currentTimeMillis();
    }

    private void sleepByTime(Integer sleepTime) {
        sleepTime = getSleepTimeIfNull(sleepTime, 0);
        try {
            if(sleepTime >0) {
                Thread.sleep(sleepTime * FrequencyConstant.TIME_MILLISECOND_TO_SECOND);
            }
        } catch (InterruptedException e) {
            log.warn("---sleepByTime--Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new FailedException(e.getMessage());
        }
    }

    @GetMapping("/waitLockTestOrderEtcdLock")
    @RequestLock(name = "waitLockTestOrderEtcdLock", attrName = "orderId", time = 5, errMsg = "任务%s正在执行", lockType = ReqLockType.ETCD_LOCK)
    public Object waitLockTestOrderEtcdLock(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        log.info("----waitLockTestOrderEtcdSync--userId={} orderId={} ip={}", request.getParameter(RequestParams.USER_ID), orderId, RequestUtils.getIpAddress(request));
        sleepByTime(sleepTime);
        return System.currentTimeMillis();
    }

    @GetMapping("/waitLockTestOrderZk")
    @RequestLock(name = "waitLockTestOrderZk", attrName = "orderId", time = 5, errMsg = "任务%s正在执行", lockType = ReqLockType.ZK)
    public Object waitLockTestOrderZk(@RequestParam(name = "sleepTime", required = false) Integer sleepTime, HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        log.info("----waitLockTestOrderZk--userId={} orderId={} ip={}", request.getParameter(RequestParams.USER_ID), orderId, RequestUtils.getIpAddress(request));
        sleepByTime(sleepTime);
        return System.currentTimeMillis();
    }
}
