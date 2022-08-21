package org.ccs.opendfl.locks.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.ReqLockType;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 分布式锁测试
 *
 * @author chenjh
 */
@RestController
@RequestMapping("/lockTest")
@Slf4j
public class LockTestController {
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
     * @param sleepTime 休眼时间秒数
     * @param request   HttpServletRequest
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
     * @param request   HttpServletRequest
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
            if (sleepTime > 0) {
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
