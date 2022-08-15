package org.ccs.opendfl.mysql.dfllogs.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.config.MysqlConfiguration;
import org.ccs.opendfl.mysql.dflcore.biz.IDflFrequencyBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflLogUserBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflOutLimitLogBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflRequestScansBiz;
import org.ccs.opendfl.mysql.dfllogs.po.DflLogUserPo;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLimitLogPo;
import org.ccs.opendfl.mysql.dfllogs.po.DflRequestScansPo;
import org.ccs.opendfl.mysql.dfllogs.vo.DflOutLimitLogCountVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DflOutLimitLogcontroller
 * 频率限制超限日志 Controller
 *
 * @author chenjh
 * @date 2022-5-6 23:21:44
 */
@RestController
@RequestMapping("/dflLogs/dflOutLimitLog")
public class DflOutLimitLogController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflOutLimitLogController.class);

    @Autowired
    private IDflOutLimitLogBiz dflOutLimitLogBiz;
    @Autowired
    private IDflRequestScansBiz dflRequestScansBiz;
    @Autowired
    private IDflLogUserBiz dflLogUserBiz;
    @Autowired
    private IDflFrequencyBiz dflFrequencyBiz;
    @Autowired
    private MysqlConfiguration mysqlConfiguration;

    @GetMapping(value = {"/index"})
    public String index() {
        return "mysql/mysql/dflOutLimitLog";
    }

    /**
     * 频率限制超限日志列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo<> 翻页结果
     * @author chenjh
     * @date 2022-5-6 23:21:44
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflOutLimitLogPo> queryPage(HttpServletRequest request, DflOutLimitLogPo entity, MyPageInfo<DflOutLimitLogPo> pageInfo) {
        if (entity == null) {
            entity = new DflOutLimitLogPo();
        }
        Map<String, Object> params = this.createAllParams(request);
        ValidateUtils.checkTimeDateLimit(params, "uri,ip,uid,userId");
        if (this.mysqlConfiguration.getUserIdToNum() == 1) {
            if (StringUtils.isNotBlank(entity.getUserId())) {
                entity.setUid(this.dflLogUserBiz.getUid(entity.getUserId(), null, null));
                entity.setUserId(null);
                params.put("uid", entity.getUid());
                params.remove("userId");
            }
        }
        if (StringUtils.isNotBlank(entity.getUri())) {
            Integer uriId = dflRequestScansBiz.getUriId(entity.getUri());
            if (uriId == null) {
                uriId = -1;
            }
            entity.setUriId(uriId);
            params.put("uriId", uriId);
            params.remove("uri");
        }
        if (StringUtils.isNotBlank(entity.getIp())) {
            entity.setIp(RequestUtils.convertIpv4(entity.getIp()));
            params.put("ip", entity.getIp());
        }
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflOutLimitLogBiz.findPageBy(entity, pageInfo, params);
        List<DflOutLimitLogPo> list = pageInfo.getList();

        final Map<Long, DflLogUserPo> userMap = new HashMap<>(list.size());
        if (mysqlConfiguration.getUserIdToNum() == 1) {
            List<Long> userIdList = list.stream().filter(t -> t.getUid() != null).map(DflOutLimitLogPo::getUid).distinct().collect(Collectors.toList());
            userMap.putAll(dflLogUserBiz.getUserPos(userIdList));
        }

        List<Integer> uriIdList = list.stream().filter(t -> t.getUriId() != null).map(DflOutLimitLogPo::getUriId).distinct().collect(Collectors.toList());
        Map<Integer, DflRequestScansPo> uriMap = dflRequestScansBiz.getUriPos(uriIdList);

        List<Integer> frequencyIdList = list.stream().filter(t -> t.getFrequencyId() != null).map(DflOutLimitLogPo::getFrequencyId).distinct().collect(Collectors.toList());
        Map<Integer, DflFrequencyPo> frequencyMap = dflFrequencyBiz.getFrequencyByIds(frequencyIdList);

        list.stream().forEach(t -> {
            t.setUser(userMap.get(t.getUid()));
            t.setUriPo(uriMap.get(t.getUriId()));
            t.setFrequency(frequencyMap.get(t.getFrequencyId()));
            if (t.getUriPo() != null) {
                t.setUri(t.getUriPo().getUri());
            }
            if (StringUtils.isNumeric(t.getIp())) {
                t.setIp(RequestUtils.getNumConvertIp(Long.parseLong(t.getIp())));
            }
        });
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflOutLimitLogPo> queryPage2(HttpServletRequest request, DflOutLimitLogPo entity, MyPageInfo<DflOutLimitLogPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO<>(pageInfo);
    }


    @RequestMapping(value = "/listCount", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflOutLimitLogCountVo> listCount(HttpServletRequest request, DflOutLimitLogPo entity, MyPageInfo<DflOutLimitLogCountVo> pageInfo) {
        this.pageSortBy(pageInfo);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        if (entity == null) {
            entity = new DflOutLimitLogPo();
        }
        Map<String, Object> paramsMap = this.createAllParams(request);
        if (StringUtils.isNotBlank(entity.getUserId())) {
            Long uid = dflLogUserBiz.getUid(entity.getUserId(), null, null);
            if (uid == null) {
                uid = -1L;
            }
            entity.setUid(uid);
        }
        if (StringUtils.isNotBlank(entity.getUri())) {
            Integer uriId = dflRequestScansBiz.getUriId(entity.getUri());
            if (uriId == null) {
                uriId = -1;
            }
            entity.setUriId(uriId);
        }
        if (StringUtils.isNotBlank(entity.getIp())) {
            entity.setIp(RequestUtils.convertIpv4(entity.getIp()));
            paramsMap.put("ip", entity.getIp());
        }
        List<DflOutLimitLogCountVo> list = dflOutLimitLogBiz.countFreqLogs(entity, paramsMap, pageInfo);
        List<Long> userUidList = list.stream().filter(t -> t.getUid() != null).map(DflOutLimitLogCountVo::getUid).collect(Collectors.toList());
        Map<Long, DflLogUserPo> userMap = dflLogUserBiz.getUserPos(userUidList);

        List<Integer> uriIdList = list.stream().filter(t -> t.getUriId() != null).map(DflOutLimitLogCountVo::getUriId).distinct().collect(Collectors.toList());
        Map<Integer, DflRequestScansPo> uriMap = dflRequestScansBiz.getUriPos(uriIdList);
        list.forEach(t -> {
            if (t.getUid() != null) {
                t.setUser(userMap.get(t.getUid()));
            }
            DflRequestScansPo scansPo = uriMap.get(t.getUriId());
            if (scansPo != null) {
                t.setUri(scansPo.getUri());
            }
        });
        pageInfo.setList(list);
        pageInfo.setPages(100);
        return new PageVO<>(pageInfo);
    }

    /**
     * 频率限制超限日志 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:21:44
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData edit(DflOutLimitLogPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        dflOutLimitLogBiz.saveDflOutLimitLog(entity);
        return ResultData.success();
    }

    /**
     * 频率限制超限日志 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:21:44
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData update(DflOutLimitLogPo entity, HttpServletRequest request) {
        int v = dflOutLimitLogBiz.updateDflOutLimitLog(entity);
        return ResultData.success(v);
    }

    /**
     * 频率限制超限日志 删除
     *
     * @param request
     * @param dflOutLimitLog
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:21:44
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData delete(DflOutLimitLogPo dflOutLimitLog, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflOutLimitLogBiz.deleteDflOutLimitLog(dflOutLimitLog.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}