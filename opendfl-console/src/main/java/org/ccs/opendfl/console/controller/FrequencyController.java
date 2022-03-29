package org.ccs.opendfl.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IUserBiz;
import org.ccs.opendfl.core.config.vo.RolePermitVo;
import org.ccs.opendfl.core.config.vo.UserVo;
import org.ccs.opendfl.core.exception.PermissionDeniedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.limitcount.FrequencyEvictUtil;
import org.ccs.opendfl.core.limitcount.FrequencyHandlerInterceptor;
import org.ccs.opendfl.core.limitcount.FrequencyLoginUtils;
import org.ccs.opendfl.core.limitcount.FrequencyUtils;
import org.ccs.opendfl.core.limitlock.RequestLockHandlerInterceptor;
import org.ccs.opendfl.core.utils.*;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.ccs.opendfl.core.vo.RequestShowVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/frequency")
@Slf4j
public class FrequencyController {
    public static final String INSUFFICIENT_USER_PERMISSIONS = "Insufficient user permissions";
    @Autowired
    private IUserBiz userBiz;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final Integer TIME_NULL = null;


    @GetMapping("/ipConvert")
    @ResponseBody
    public Object ipConvert(HttpServletRequest request) {
        String ip = request.getParameter("ip");
        ValidateUtils.notNull(ip, "ip is null");
        if (StringUtils.isNumeric(ip)) {
            return RequestUtils.getNumConvertIp(Long.parseLong(ip));
        }
        return RequestUtils.getIpConvertNum(ip);
    }


    @ResponseBody
    @RequestMapping(value = "/requests", method = {RequestMethod.POST, RequestMethod.GET})
    public Object requests(HttpServletRequest request) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = FrequencyLoginUtils.getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        RolePermitVo rolePermitVo = FrequencyLoginUtils.getRolePermit(userVo.getRole());
        if (rolePermitVo.getIfView() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }

        String resetFreqMap = request.getParameter("resetRequestMap");
        if (StringUtils.equals(resetFreqMap, "clearRequest")) {
            if (rolePermitVo.getIfClear() != 1) {
                FrequencyUtils.addAuditLog(request, userVo, resetFreqMap, "fail", TIME_NULL);
                throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
            }
            FrequencyUtils.addAuditLog(request, userVo, resetFreqMap, "success", TIME_NULL);
            FrequencyHandlerInterceptor.requestVoMap.clear();
        }

        FrequencyUtils.addAuditLog(request, userVo, "list", "ok", TIME_NULL);
        Collection<RequestVo> list = FrequencyHandlerInterceptor.requestVoMap.values();
        List<RequestShowVo> showList = toRequestShowList(list);
        return ResultData.success(showList);
    }

    private List<RequestShowVo> toRequestShowList(Collection<RequestVo> list) {
        List<RequestShowVo> showList = new ArrayList<>(list.size());
        Collection<FrequencyVo> frequencyList = FrequencyHandlerInterceptor.freqMap.values();
        Collection<RequestLockVo> lockList = RequestLockHandlerInterceptor.locksMap.values();
        list.forEach(t -> {
            RequestShowVo showVo = new RequestShowVo();
            BeanUtils.copyProperties(t, showVo);
            StringBuilder limitTypes = new StringBuilder();
            List<FrequencyVo> tmpList = new ArrayList<>();
            for (FrequencyVo freq : frequencyList) {
                if (StringUtils.equals(t.getRequestUri(), freq.getRequestUri())) {
                    tmpList.add(freq);
                    limitTypes.append(freq.getLimitType()).append(",");
                }
            }
            showVo.setLimitFrequencys(tmpList);

            List<RequestLockVo> tmpLockList = new ArrayList<>();
            for (RequestLockVo lock : lockList) {
                if (StringUtils.equals(t.getRequestUri(), lock.getRequestUri())) {
                    tmpLockList.add(lock);
                    limitTypes.append("lock");
                }
            }
            showVo.setLocks(tmpLockList);
            String limitTypeStr = CommUtils.removeEndComma(limitTypes.toString());
            showVo.setLimitTypes(limitTypeStr);
            showList.add(showVo);
        });
        return showList;
    }

    @ResponseBody
    @RequestMapping(value = "/limits", method = {RequestMethod.POST, RequestMethod.GET})
    public Object limits(HttpServletRequest request) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = FrequencyLoginUtils.getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        RolePermitVo rolePermitVo = FrequencyLoginUtils.getRolePermit(userVo.getRole());
        if (rolePermitVo.getIfView() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }

        String resetFreqMap = request.getParameter("resetFreqMap");
        if (StringUtils.equals(resetFreqMap, "clearFreq")) {
            if (rolePermitVo.getIfClear() != 1) {
                FrequencyUtils.addAuditLog(request, userVo, resetFreqMap, "fail", TIME_NULL);
                throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
            }
            FrequencyUtils.addAuditLog(request, userVo, resetFreqMap, "success", TIME_NULL);
            FrequencyHandlerInterceptor.freqMap.clear();
        }

        FrequencyUtils.addAuditLog(request, userVo, "list", "ok", TIME_NULL);
        Collection<FrequencyVo> list = FrequencyHandlerInterceptor.freqMap.values();
        return ResultData.success(list);
    }

    @ResponseBody
    @RequestMapping(value = "/locks", method = {RequestMethod.POST, RequestMethod.GET})
    public Object locks(HttpServletRequest request) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = FrequencyLoginUtils.getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        RolePermitVo rolePermitVo = FrequencyLoginUtils.getRolePermit(userVo.getRole());
        if (rolePermitVo.getIfView() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }


        String resetFreqMap = request.getParameter("resetLockMap");
        if (StringUtils.equals(resetFreqMap, "clearLock")) {
            if (rolePermitVo.getIfClear() != 1) {
                FrequencyUtils.addAuditLog(request, userVo, resetFreqMap, "fail", TIME_NULL);
                throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
            }
            FrequencyUtils.addAuditLog(request, userVo, resetFreqMap, "success", TIME_NULL);
            RequestLockHandlerInterceptor.locksMap.clear();
        }

        FrequencyUtils.addAuditLog(request, userVo, "list", "ok", TIME_NULL);
        Collection<RequestLockVo> list = RequestLockHandlerInterceptor.locksMap.values();
        return ResultData.success(list);
    }

    @ResponseBody
    @RequestMapping(value = "/evict", method = {RequestMethod.POST, RequestMethod.GET})
    public Object evict(HttpServletRequest request, FrequencyVo frequency) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = FrequencyLoginUtils.getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        RolePermitVo rolePermitVo = FrequencyLoginUtils.getRolePermit(userVo.getRole());
        if (rolePermitVo.getIfEvict() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }

        String userId = request.getParameter(RequestParams.USER_ID);
        ValidateUtils.notNull(frequency.getName(), "name is null");
        ValidateUtils.notNull(frequency.getTime(), "time is null");
        ValidateUtils.notNull(userId, "userId is null");

        String userIdByCode = userBiz.getUserId(userId);
        if (userIdByCode != null) {
            userId = userIdByCode;
        }
        FrequencyUtils.addAuditLog(request, userVo, "evict", userId, frequency.getTime());
        String evictKey = FrequencyEvictUtil.freqEvict(frequency, userId, redisTemplate);
        return ResultData.success(evictKey);
    }

    @ResponseBody
    @RequestMapping(value = "/evictTimes", method = {RequestMethod.POST, RequestMethod.GET})
    public Object evictTimes(HttpServletRequest request, FrequencyVo frequency) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = FrequencyLoginUtils.getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        RolePermitVo rolePermitVo = FrequencyLoginUtils.getRolePermit(userVo.getRole());
        if (rolePermitVo.getIfEvict() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }

        String times = request.getParameter("times");
        String userId = request.getParameter(RequestParams.USER_ID);
        ValidateUtils.notNull(frequency.getName(), "name is null");
        ValidateUtils.notNull(userId, "userId is null");

        String userIdByCode = userBiz.getUserId(userId);

        String[] timeArr = times.split(",");
        List<Integer> timeList = new ArrayList<>();
        for (String timeStr : timeArr) {
            if (StringUtils.isBlank(timeStr)) {
                continue;
            }
            timeList.add(Integer.parseInt(timeStr));
        }

        if (userIdByCode != null) {
            userId = userIdByCode;
        }
        FrequencyUtils.addAuditLog(request, userVo, "evict", userId, timeList.toArray(new Integer[0]));
        List<String> infoList = FrequencyEvictUtil.freqEvictList(frequency.getName(), timeList, userId, redisTemplate);
        return ResultData.success(infoList);
    }

    @ResponseBody
    @RequestMapping(value = "/evictIpUser", method = {RequestMethod.POST, RequestMethod.GET})
    public Object evictIpUser(HttpServletRequest request, FrequencyVo frequency) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = FrequencyLoginUtils.getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        RolePermitVo rolePermitVo = FrequencyLoginUtils.getRolePermit(userVo.getRole());
        if (rolePermitVo.getIfEvict() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }

        String ip = request.getParameter("ip");
        ValidateUtils.notNull(ip, "ip is null");
        ValidateUtils.notNull(frequency.getName(), "name is null");
        ValidateUtils.notNull(frequency.getTime(), "time is null");
        ip = "" + RequestUtils.getIpConvertNum(ip);
        FrequencyUtils.addAuditLog(request, userVo, "evict", ip, frequency.getTime());
        String evictKey = FrequencyEvictUtil.freqIpUserEvict(frequency, ip, redisTemplate);
        return ResultData.success(evictKey);
    }


    @ResponseBody
    @RequestMapping(value = "/evictUserIp", method = {RequestMethod.POST, RequestMethod.GET})
    public Object evictUserIp(HttpServletRequest request, FrequencyVo frequency) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = FrequencyLoginUtils.getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        RolePermitVo rolePermitVo = FrequencyLoginUtils.getRolePermit(userVo.getRole());
        if (rolePermitVo.getIfEvict() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }

        String userId = request.getParameter(RequestParams.USER_ID);
        ValidateUtils.notNull(frequency.getName(), "name is null");
        ValidateUtils.notNull(frequency.getTime(), "time is null");
        ValidateUtils.notNull(userId, "userId is null");

        String userIdByCode = userBiz.getUserId(userId);
        if (userIdByCode != null) {
            userId = userIdByCode;
        }
        FrequencyUtils.addAuditLog(request, userVo, "evict", userId, frequency.getTime());
        String evictKey = FrequencyEvictUtil.freqUserIpEvict(frequency, userId, redisTemplate);
        return ResultData.success(evictKey);
    }


}
