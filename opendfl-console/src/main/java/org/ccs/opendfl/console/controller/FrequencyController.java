package org.ccs.opendfl.console.controller;


import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.console.biz.IFrequencyLoginBiz;
import org.ccs.opendfl.console.config.vo.RolePermitVo;
import org.ccs.opendfl.console.config.vo.UserVo;
import org.ccs.opendfl.console.constant.UserOperType;
import org.ccs.opendfl.console.utils.AuditLogUtils;
import org.ccs.opendfl.core.biz.*;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.RunCountType;
import org.ccs.opendfl.core.exception.PermissionDeniedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.limitfrequency.FrequencyHandlerInterceptor;
import org.ccs.opendfl.core.limitlock.RequestLockHandlerInterceptor;
import org.ccs.opendfl.core.utils.*;
import org.ccs.opendfl.core.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    @Resource(name = "requestLockDataRedisBiz")
    private IRequestLockDataBiz requestLockDataBiz;
    @Resource(name = "frequencyLoginRedisBiz")
    private IFrequencyLoginBiz frequencyLoginBiz;
    @Autowired
    private IMaxRunTimeBiz maxRunTimeBiz;
    @Autowired
    private IRunCountBiz runCountBiz;
    @Autowired
    private IOutLimitCountBiz outLimitCountBiz;
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    private static final Integer TIME_NULL = null;

    /**
     * IP转数字转换
     *
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/ipConvert")
    public Object ipConvert(HttpServletRequest request) {
        String ip = request.getParameter("ip");
        ValidateUtils.notNull(ip, "ip is null");
        if (StringUtils.isNumeric(ip)) {
            return RequestUtils.getNumConvertIp(Long.parseLong(ip));
        }
        return RequestUtils.getIpConvertNum(ip);
    }

    /**
     * 日期选择下拉框
     *
     * @param request
     * @return List<ComboxItemVo>
     */
    @ResponseBody
    @RequestMapping(value = "/getRunDays", method = {RequestMethod.GET, RequestMethod.POST})
    public List<ComboxItemVo> getRunDays(HttpServletRequest request) {
        return runCountBiz.getRunDays();
    }

    /**
     * 限制次数类型
     *
     * @param request
     * @return List<ComboxItemVo>
     */
    @ResponseBody
    @RequestMapping(value = "/getRunCountTypeByDay", method = {RequestMethod.GET, RequestMethod.POST})
    public List<ComboxItemVo> getRunCountTypeByDay(HttpServletRequest request
            , @RequestParam(value = "day", required = false, defaultValue = "0") Integer day) {
        List<ComboxItemVo> list = new ArrayList<>();
        list.add(new ComboxItemVo("current", "当前本机", true));
        if (frequencyConfiguration.getRunCountCacheDay() == 0) {
            list.add(new ComboxItemVo("current", "frequency.runCountCacheDay=0 Closed", false));
            return list;
        }
        list.addAll(runCountBiz.getRunCountTypeByDay(day));
        list.addAll(outLimitCountBiz.getRunCountTypeByDay(day));
        return list;
    }


    /**
     * 有调用的接口信息
     * 有调用才会有记录
     *
     * @param request
     * @param requestVo
     * @return
     */
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
        if(StringUtils.isNotBlank(requestVo.getRequestUri())){
            list=list.stream().filter(t->t.getRequestUri().contains(requestVo.getRequestUri())).collect(Collectors.toList());
        }
        List<RequestShowVo> showList = toRequestShowList(list, requestVo);
        return ResultData.success(showList);
    }

    private void requestRunCount(String type, Integer day, List<RequestShowVo> showList) {
        if (StringUtils.equals("current", type) && day == -1) {
            return;
        }
        if (StringUtils.equals("current", type)) {
            type = RunCountType.COUNT.getCode();
        }
        Long curTime = System.currentTimeMillis();
        if (day > 0) {
            curTime = curTime - day * FrequencyConstant.TIME_MILLISECOND_TO_HOUR * 24;
        }
        requestMaxRunTimeByDay(showList, curTime);

        RunCountType countType = RunCountType.parse(type);
        if (countType != null) {
            requestRunCount(countType, showList, curTime);
        } else {
            FreqLimitType limitType = FreqLimitType.parseCode(type);
            requestOutLimitCount(limitType, showList, curTime);
        }
    }

    /**
     * 接口访问最大执行时间查询
     * @param showList
     * @param curTime
     */
    private void requestMaxRunTimeByDay(List<RequestShowVo> showList, Long curTime) {
        List<MaxRunTimeVo> list = maxRunTimeBiz.getNewlyMaxRunTime(curTime, 24 * 3600, 100);
        for (RequestShowVo showVo : showList) {
            for (MaxRunTimeVo runTimeVo : list) {
                if (StringUtils.equals(showVo.getRequestUri(), runTimeVo.getUri())) {
                    showVo.setMaxRunTime(runTimeVo.getMaxRunTime());
                    showVo.setMaxRunTimeCreateTime(runTimeVo.getCreateTime());
                    break;
                }
            }
        }
    }

    private void requestRunCount(RunCountType countType, List<RequestShowVo> showList, Long curTime) {
        if (countType == null) {
            return;
        }
        List<RequestShowVo> tmpList = new ArrayList<>();
        List<RunCountVo> countList = runCountBiz.getNewlyRunCount(countType, curTime, 100);
        List<RunCountVo> outLimitList = runCountBiz.getNewlyRunCount(RunCountType.OUT_LIMIT, curTime, 100);

        for (RequestShowVo showVo : showList) {
            for (RunCountVo countVo : countList) {
                if (StringUtils.equals(showVo.getRequestUri(), countVo.getUri())) {
                    showVo.setCounter(new AtomicInteger(countVo.getCount()));
                    showVo.setLimitCounter(new AtomicInteger(0));
                    tmpList.add(showVo);
                    break;
                }
            }
            for (RunCountVo countVo : outLimitList) {
                if (StringUtils.equals(showVo.getRequestUri(), countVo.getUri())) {
                    showVo.setLimitCounter(new AtomicInteger(countVo.getCount()));
                    break;
                }
            }
        }
        showList.clear();
        showList.addAll(tmpList);
    }

    private void requestOutLimitCount(FreqLimitType limitType, List<RequestShowVo> showList, Long curTime) {
        if (limitType == null) {
            return;
        }
        List<RequestShowVo> tmpList = new ArrayList<>();

        List<RunCountVo> countList = runCountBiz.getNewlyRunCount(RunCountType.COUNT, curTime, 100);
        List<RunCountVo> outLimitList = outLimitCountBiz.getNewlyRunCount(limitType, curTime, 100);

        for (RequestShowVo showVo : showList) {
            for (RunCountVo countVo : countList) {
                if (StringUtils.equals(showVo.getRequestUri(), countVo.getUri())) {
                    showVo.setCounter(new AtomicInteger(countVo.getCount()));
                    showVo.setLimitCounter(new AtomicInteger(0));
                    break;
                }
            }
            for (RunCountVo countVo : outLimitList) {
                if (StringUtils.equals(showVo.getRequestUri(), countVo.getUri())) {
                    showVo.setLimitCounter(new AtomicInteger(countVo.getCount()));
                    tmpList.add(showVo);
                    break;
                }
            }
        }
        showList.clear();
        showList.addAll(tmpList);
    }

    /**
     * 从类中直接加载所有controller类的接口信息
     * 并可与正在运行的接口关联
     *
     * @param request   HttpServletRequest
     * @param requestVo RequestVo
     * @return ResultData
     */
    @ResponseBody
    @RequestMapping(value = "/requestScans", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData requestScans(HttpServletRequest request, RequestVo requestVo
            , @RequestParam(value = "type", required = false, defaultValue ="current") String type
            , @RequestParam(value = "day", required = false, defaultValue = "-1") Integer day) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        checkUserPermission(userVo, UserOperType.VIEW);
        String pkg = request.getParameter("pkg");
        pkg = (String) CommUtils.nvl(pkg, "org.ccs.opendfl");
        AuditLogUtils.addAuditLog(request, userVo, "list", "ok", TIME_NULL);
        List<RequestVo> list = AnnotationControllerUtils.getControllerRequests(pkg);
        if(StringUtils.isNotBlank(requestVo.getRequestUri())){
            list=list.stream().filter(t->t.getRequestUri().contains(requestVo.getRequestUri())).collect(Collectors.toList());
        }
        List<RequestShowVo> showList = addRequestRunTime(list);
        requestRunCount(type, day, showList);
        return ResultData.success(showList);
    }

    private List<RequestShowVo> addRequestRunTime(List<RequestVo> list) {
        //把接口调用情况更新过来
        final Collection<RequestVo> requestList = FrequencyHandlerInterceptor.requestVoMap.values();
        return list.stream().map(t -> {
            RequestShowVo showVo = (RequestShowVo) t;
            showVo.setMaxRunTime(0L);
            showVo.setMaxRunTimeCreateTime(null);
            for (RequestVo req : requestList) {
                if (StringUtils.equals(t.getRequestUri(), req.getRequestUri())) {
                    showVo.setCounter(req.getCounter());
                    showVo.setLimitCounter(req.getLimitCounter());
                    showVo.setMaxRunTime(req.getMaxRunTime());
                    showVo.setMaxRunTimeCreateTime(req.getMaxRunTimeCreateTime());
                    showVo.setCreateTime(req.getCreateTime());
                    break;
                }
            }
            return showVo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询最近时间内接口调后时间最大的数据
     *
     * @param request   HttpServletRequest
     * @param requestVo RequestVo
     * @return ResultData
     */
    @ResponseBody
    @RequestMapping(value = "/requestMaxRunTimes", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData requestMaxRunTimes(HttpServletRequest request, RequestVo requestVo
            , @RequestParam(name = "second", defaultValue = "10") Integer second, @RequestParam(name = "count", defaultValue = "20") Integer count) {
        String token = RequestUtils.getToken(request);
        UserVo userVo = frequencyLoginBiz.getUserByToken(token);
        checkUserPermission(userVo, UserOperType.VIEW);
        String pkg = request.getParameter("pkg");
        pkg = (String) CommUtils.nvl(pkg, "org.ccs.opendfl");
        List<MaxRunTimeVo> maxRunTimes = this.maxRunTimeBiz.getNewlyMaxRunTime(second, count);

        List<RequestVo> list = AnnotationControllerUtils.getControllerRequests(pkg);
        List<RequestShowVo> showList = addRequestRunTime(list);
        List<RequestShowVo> showMaxList = new ArrayList<>();
        for (RequestShowVo showVo : showList) {
            for (MaxRunTimeVo maxRunTimeVo : maxRunTimes) {
                if (StringUtils.equals(showVo.getRequestUri(), maxRunTimeVo.getUri())) {
                    showVo.setMaxRunTime(maxRunTimeVo.getMaxRunTime());
                    showVo.setMaxRunTimeCreateTime(maxRunTimeVo.getCreateTime());
                    showMaxList.add(showVo);
                    break;
                }
            }
        }
//        Long curTime = System.currentTimeMillis()-second*FrequencyConstant.TIME_MILLISECOND_TO_SECOND;
//        requestRunCount(RunCountType.COUNT, null, showMaxList,curTime);
        return ResultData.success(showMaxList);
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
            showVo.setCounter(new AtomicInteger(t.getCounter().get()));
            showVo.setLimitCounter(new AtomicInteger(t.getLimitCounter().get()));
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

    /**
     * 频率限制查询
     *
     * @param request   HttpServletRequest
     * @param frequency FrequencyVo
     * @param ip        IP地址
     * @param userId    用户ID
     * @return
     */
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

    /**
     * 分布式锁接口信息
     *
     * @param request HttpServletRequest
     * @param lockVo  RequestLockVo
     * @param userId  String
     * @return
     */
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
            list = requestLockDataBiz.requestLocks(userId);
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
        String evictKey = requestLockDataBiz.lockEvict(lockVo.getName(), userId);
        return ResultData.success(evictKey);
    }
}
