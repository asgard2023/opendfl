package org.ccs.opendfl.mysql.dfllogs.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflAuditLogBiz;
import org.ccs.opendfl.mysql.dfllogs.po.DflAuditLogPo;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserBiz;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Version V1.0
 * @Title: DflAuditLogcontroller
 * @Description: 后台管理审计日志 Controller
 * @Author: Created by chenjh
 * @Date: 2022-5-6 23:20:31
 */
@RestController
@RequestMapping("/dflLogs/dflAuditLog")
public class DflAuditLogController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflAuditLogController.class);

    @Autowired
    private IDflAuditLogBiz dflAuditLogBiz;
    @Autowired
    private IDflUserBiz dflUserBiz;

    @GetMapping(value = {"/index"})
    public String index() {
        return "mysql/mysql/dflAuditLog";
    }

    /**
     * 后台管理审计日志列表查询
     *
     * @param request
     * @param entity
     * @param pageInfo
     * @return java.lang.Object
     * @author chenjh
     * @date 2022-5-6 23:20:31
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public MyPageInfo<DflAuditLogPo> queryPage(HttpServletRequest request, DflAuditLogPo entity, MyPageInfo<DflAuditLogPo> pageInfo) {
        if (entity == null) {
            entity = new DflAuditLogPo();
        }
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflAuditLogBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        List<DflAuditLogPo> list = pageInfo.getList();
        List<Integer> userIdList = list.stream().filter(t -> t.getUserId() != null).map(DflAuditLogPo::getUserId).distinct().collect(Collectors.toList());
        Map<Integer, UserVo> userMap = dflUserBiz.getUserMapByIds(userIdList);
        list.stream().forEach(t -> {
            t.setUser(userMap.get(t.getUserId()));
        });
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public PageVO<DflAuditLogPo> queryPage2(HttpServletRequest request, DflAuditLogPo entity, MyPageInfo<DflAuditLogPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 后台管理审计日志 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:20:31
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData edit(DflAuditLogPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        dflAuditLogBiz.saveDflAuditLog(entity);
        return ResultData.success();
    }

    /**
     * 后台管理审计日志 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:20:31
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(DflAuditLogPo entity, HttpServletRequest request) {
        int v = dflAuditLogBiz.updateDflAuditLog(entity);
        return ResultData.success(v);
    }

    /**
     * 后台管理审计日志 删除
     *
     * @param request
     * @param dflAuditLog
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:20:31
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData delete(DflAuditLogPo dflAuditLog, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflAuditLogBiz.deleteDflAuditLog(dflAuditLog.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}