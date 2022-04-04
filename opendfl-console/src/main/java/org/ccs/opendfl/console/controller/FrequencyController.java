package org.ccs.opendfl.console.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.console.biz.IFrequencyDataBiz;
import org.ccs.opendfl.console.biz.IFrequencyLoginBiz;
import org.ccs.opendfl.console.config.vo.RolePermitVo;
import org.ccs.opendfl.console.config.vo.UserVo;
import org.ccs.opendfl.console.constant.UserOperType;
import org.ccs.opendfl.console.utils.AuditLogUtils;
import org.ccs.opendfl.core.biz.IUserBiz;
import org.ccs.opendfl.core.exception.PermissionDeniedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.limitfrequency.FrequencyHandlerInterceptor;
import org.ccs.opendfl.core.limitlock.RequestLockHandlerInterceptor;
import org.ccs.opendfl.core.utils.*;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.ccs.opendfl.core.vo.RequestShowVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 限数数据查询及处理
 *
 * @author chenjh
 */
@RestController
@RequestMapping("/frequency")
@Slf4j
public class FrequencyController {
    public static final String INSUFFICIENT_USER_PERMISSIONS = "Insufficient user permissions";
    @Autowired
    private IUserBiz userBiz;
    @Resource(name = "frequencyDataRedisBiz")
    private IFrequencyDataBiz frequencyDataBiz;
    @Resource(name = "frequencyLoginRedisBiz")
    private IFrequencyLoginBiz frequencyLoginBiz;
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
    public ResultData requests(HttpServletRequest request, RequestVo requestVo) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        checkUserPermission(userVo, UserOperType.VIEW);

        String clearRequestType = request.getParameter("clearRequestType");
        if (StringUtils.equals(clearRequestType, "clearRequest")) {
            RolePermitVo rolePermitVo = frequencyLoginBiz.getRolePermit(userVo.getRole());
            if (rolePermitVo.getIfClear() != 1) {
                AuditLogUtils.addAuditLog(request, userVo, clearRequestType, "fail", TIME_NULL);
                throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
            }
            AuditLogUtils.addAuditLog(request, userVo, clearRequestType, "success", TIME_NULL);
            FrequencyHandlerInterceptor.requestVoMap.clear();
        }

        AuditLogUtils.addAuditLog(request, userVo, "list", "ok", TIME_NULL);
        Collection<RequestVo> list = FrequencyHandlerInterceptor.requestVoMap.values();
        List<RequestShowVo> showList = toRequestShowList(list, requestVo);
        return ResultData.success(showList);
    }

    private List<RequestShowVo> toRequestShowList(Collection<RequestVo> list, RequestVo requestVo) {
        List<RequestShowVo> showList = new ArrayList<>(list.size());
        Collection<FrequencyVo> frequencyList = FrequencyHandlerInterceptor.freqMap.values();
        final Collection<FrequencyVo> frequencyListSorted = frequencyList.stream()
                .sorted(Comparator.comparing(FrequencyVo::getName).thenComparing(FrequencyVo::getTime)).collect(Collectors.toList());
        Collection<RequestLockVo> lockList = RequestLockHandlerInterceptor.locksMap.values();
        list.forEach(t -> {
            if (StringUtils.isNotBlank(requestVo.getRequestUri()) && !t.getRequestUri().contains(requestVo.getRequestUri())) {
                return;
            }
            if (StringUtils.isNotBlank(requestVo.getMethod()) && !StringUtils.equals(t.getMethod(), requestVo.getMethod())) {
                return;
            }
            RequestShowVo showVo = new RequestShowVo();
            BeanUtils.copyProperties(t, showVo);
            StringBuilder limitTypes = new StringBuilder();
            StringBuilder attrNames = new StringBuilder();
            relFrequencys(frequencyListSorted, t, showVo, limitTypes, attrNames);
            relLocks(lockList, t, showVo, limitTypes, attrNames);
            showVo.setAttrName(CommUtils.removeEndComma(attrNames.toString()));
            String limitTypeStr = CommUtils.removeEndComma(limitTypes.toString());
            showVo.setLimitTypes(limitTypeStr);
            showList.add(showVo);
        });
        return showList;
    }

    private void relLocks(Collection<RequestLockVo> lockList, RequestVo t, RequestShowVo showVo, StringBuilder limitTypes, StringBuilder attrNames) {
        List<RequestLockVo> tmpLockList = new ArrayList<>();
        for (RequestLockVo lock : lockList) {
            if (StringUtils.equals(t.getRequestUri(), lock.getRequestUri())) {
                tmpLockList.add(lock);
                limitTypes.append("lock");
                if (StringUtils.isNotBlank(lock.getAttrName()) && attrNames.indexOf(lock.getAttrName() + ",") < 0) {
                    attrNames.append(lock.getAttrName()).append(",");
                }
            }
        }
        showVo.setLocks(tmpLockList);
    }

    private void relFrequencys(Collection<FrequencyVo> frequencyListSorted, RequestVo t, RequestShowVo showVo, StringBuilder limitTypes, StringBuilder attrNames) {
        List<FrequencyVo> tmpList = new ArrayList<>();
        for (FrequencyVo freq : frequencyListSorted) {
            if (StringUtils.equals(t.getRequestUri(), freq.getRequestUri())) {
                tmpList.add(freq);
                limitTypes.append(freq.getLimitType()).append(",");
                if (StringUtils.isNotBlank(freq.getAttrName()) && attrNames.indexOf(freq.getAttrName() + ",") < 0) {
                    attrNames.append(freq.getAttrName()).append(",");
                }
            }
        }
        showVo.setLimitFrequencys(tmpList);
    }

    @ResponseBody
    @RequestMapping(value = "/limits", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData limits(HttpServletRequest request, FrequencyVo frequency
            , @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "userId", required = false) String userId) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        checkUserPermission(userVo, UserOperType.VIEW);

        String clearFreqType = request.getParameter("clearFreqType");
        if (StringUtils.equals(clearFreqType, "clearFreq")) {
            RolePermitVo rolePermitVo = frequencyLoginBiz.getRolePermit(userVo.getRole());
            if (rolePermitVo.getIfClear() != 1) {
                AuditLogUtils.addAuditLog(request, userVo, clearFreqType, "fail", TIME_NULL);
                throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
            }
            AuditLogUtils.addAuditLog(request, userVo, clearFreqType, "success", TIME_NULL);
            FrequencyHandlerInterceptor.freqMap.clear();
        }

        Collection<FrequencyVo> list;
        if (StringUtils.isNotBlank(userId)) {
            AuditLogUtils.addAuditLog(request, userVo, "list", userId, TIME_NULL);
            list = frequencyDataBiz.limitUsers(userId);
        } else if (StringUtils.isNotBlank(ip)) {
            AuditLogUtils.addAuditLog(request, userVo, "list", ip, TIME_NULL);
            list = frequencyDataBiz.limitIps(ip);
        } else {
            list = FrequencyHandlerInterceptor.freqMap.values();
            AuditLogUtils.addAuditLog(request, userVo, "list", "ok", TIME_NULL);
        }

        boolean isBlankUri = StringUtils.isBlank(frequency.getRequestUri());
        list = list.stream().filter(f -> isBlankUri || !isBlankUri && f.getRequestUri().contains(frequency.getRequestUri()))
                .filter(f -> frequency.getTime() == 0 || frequency.getTime() > 0 && f.getTime() == frequency.getTime())
                .collect(Collectors.toList());
        return ResultData.success(list);
    }

    @ResponseBody
    @RequestMapping(value = "/locks", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData locks(HttpServletRequest request, RequestLockVo lockVo, @RequestParam(value = "userId", required = false) String userId) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        checkUserPermission(userVo, UserOperType.VIEW);


        String clearLockType = request.getParameter("clearLockType");
        if (StringUtils.equals(clearLockType, "clearLock")) {
            RolePermitVo rolePermitVo = frequencyLoginBiz.getRolePermit(userVo.getRole());
            if (rolePermitVo.getIfClear() != 1) {
                AuditLogUtils.addAuditLog(request, userVo, clearLockType, "fail", TIME_NULL);
                throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
            }
            AuditLogUtils.addAuditLog(request, userVo, clearLockType, "success", TIME_NULL);
            RequestLockHandlerInterceptor.locksMap.clear();
        }

        Collection<RequestLockVo> list;
        if (StringUtils.isNotBlank(userId)) {
            AuditLogUtils.addAuditLog(request, userVo, "list", userId, TIME_NULL);
            list = frequencyDataBiz.requestLocks(userId);
        } else {
            AuditLogUtils.addAuditLog(request, userVo, "list", "ok", TIME_NULL);
            list = RequestLockHandlerInterceptor.locksMap.values();
        }

        boolean isBlankUri = StringUtils.isBlank(lockVo.getRequestUri());
        list = list.stream().filter(f -> isBlankUri || !isBlankUri && f.getRequestUri().contains(lockVo.getRequestUri()))
                .filter(f -> lockVo.getTime() == 0 || lockVo.getTime() > 0 && f.getTime() == lockVo.getTime())
                .collect(Collectors.toList());
        return ResultData.success(list);
    }

    @ResponseBody
    @RequestMapping(value = "/evict", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData evict(HttpServletRequest request, FrequencyVo frequency) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        UserOperType operType = UserOperType.EVICT;
        checkUserPermission(userVo, operType);

        String userId = request.getParameter(RequestParams.USER_ID);
        checkInputFrequency(frequency, userId);

        String userIdByCode = userBiz.getUserId(userId);
        if (userIdByCode != null) {
            userId = userIdByCode;
        }
        AuditLogUtils.addAuditLog(request, userVo, operType.getType(), userId, frequency.getTime());
        String evictKey = frequencyDataBiz.freqEvict(frequency, userId);
        return ResultData.success(evictKey);
    }

    private void checkInputFrequency(FrequencyVo frequency, String userId) {
        ValidateUtils.notNull(frequency.getName(), "name is null");
        ValidateUtils.notNull(frequency.getTime(), "time is null");
        ValidateUtils.notNull(userId, "userId is null");
    }

    private void checkUserPermission(UserVo userVo, UserOperType operType) {
        ValidateUtils.notNull(userVo, "token invalid");
        RolePermitVo rolePermitVo = frequencyLoginBiz.getRolePermit(userVo.getRole());
        if (UserOperType.VIEW == operType && rolePermitVo.getIfView() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }
        if (UserOperType.EVICT == operType && rolePermitVo.getIfEvict() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }
        if (UserOperType.CLEAR == operType && rolePermitVo.getIfClear() != 1) {
            throw new PermissionDeniedException(INSUFFICIENT_USER_PERMISSIONS);
        }
    }

    /**
     * 批量删除频率限制
     *
     * @param frequency 频率限制信息
     * @return ResultData
     */
    @ResponseBody
    @RequestMapping(value = "/evictTimes", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData evictTimes(HttpServletRequest request, FrequencyVo frequency) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        UserOperType operType = UserOperType.EVICT;
        checkUserPermission(userVo, operType);

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
        AuditLogUtils.addAuditLog(request, userVo, operType.getType(), userId, timeList.toArray(new Integer[0]));
        List<String> infoList = frequencyDataBiz.freqEvictList(frequency.getName(), timeList, userId);
        return ResultData.success(infoList);
    }

    /**
     * 删除IP用户数限制
     *
     * @param frequency 频率限制信息
     * @return ResultData
     */
    @ResponseBody
    @RequestMapping(value = "/evictIpUser", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData evictIpUser(HttpServletRequest request, FrequencyVo frequency) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        UserOperType operType = UserOperType.EVICT;
        checkUserPermission(userVo, operType);

        String ip = request.getParameter("ip");
        ValidateUtils.notNull(ip, "ip is null");
        ValidateUtils.notNull(frequency.getName(), "name is null");
        ValidateUtils.notNull(frequency.getTime(), "time is null");
        ip = "" + RequestUtils.getIpConvertNum(ip);
        AuditLogUtils.addAuditLog(request, userVo, operType.getType(), ip, frequency.getTime());
        String evictKey = frequencyDataBiz.freqIpUserEvict(frequency, ip);
        return ResultData.success(evictKey);
    }


    /**
     * 删除用户IP数限制
     *
     * @param frequency FrequencyVo
     * @return ResultData
     */
    @ResponseBody
    @RequestMapping(value = "/evictUserIp", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData evictUserIp(HttpServletRequest request, FrequencyVo frequency) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        UserOperType operType = UserOperType.EVICT;
        checkUserPermission(userVo, operType);

        String userId = request.getParameter(RequestParams.USER_ID);
        checkInputFrequency(frequency, userId);

        String userIdByCode = userBiz.getUserId(userId);
        if (userIdByCode != null) {
            userId = userIdByCode;
        }
        AuditLogUtils.addAuditLog(request, userVo, operType.getType(), userId, frequency.getTime());
        String evictKey = frequencyDataBiz.freqUserIpEvict(frequency, userId);
        return ResultData.success(evictKey);
    }

    @ResponseBody
    @RequestMapping(value = "/lockEvict", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData lockEvict(HttpServletRequest request, RequestLockVo lockVo) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        UserOperType operType = UserOperType.EVICT;
        checkUserPermission(userVo, operType);

        String userId = request.getParameter(RequestParams.USER_ID);

        String userIdByCode = userBiz.getUserId(userId);
        if (userIdByCode != null) {
            userId = userIdByCode;
        }
        AuditLogUtils.addAuditLog(request, userVo, operType.getType(), userId, lockVo.getTime());
        String evictKey = frequencyDataBiz.lockEvict(lockVo.getName(), userId);
        return ResultData.success(evictKey);
    }
}
