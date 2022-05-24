package org.ccs.opendfl.mysql.dfllogs.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflRequestScansBiz;
import org.ccs.opendfl.mysql.dfllogs.po.DflRequestScansPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Version V1.0
 * @Title: DflRequestScanscontroller
 * @Description: 所有接口方法（通过扫码所有controller接口） Controller
 * @Author: Created by chenjh
 * @Date: 2022-5-10 22:12:23
 */
@RestController
@RequestMapping("/dflLogs/dflRequestScans")
public class DflRequestScansController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflRequestScansController.class);

    @Autowired
    private IDflRequestScansBiz dflRequestScansBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/mysql/dflRequestScans";
    }

    /**
     * 所有接口方法（通过扫码所有controller接口）列表查询
     *
     * @param request
     * @param entity
     * @param pageInfo
     * @return java.lang.Object
     * @author chenjh
     * @date 2022-5-10 22:12:23
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflRequestScansPo> queryPage(HttpServletRequest request, DflRequestScansPo entity, MyPageInfo<DflRequestScansPo> pageInfo) {
        if (entity == null) {
            entity = new DflRequestScansPo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflRequestScansBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflRequestScansPo> queryPage2(HttpServletRequest request, DflRequestScansPo entity, MyPageInfo<DflRequestScansPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 扫码controller接口并更新数据
     *
     * @param request
     * @return ResultData
     * @author chenjh
     * @date 2022-5-10 22:12:23
     */
    @RequestMapping(value = "/updateScanController", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData updateScanController(HttpServletRequest request, @RequestParam(value = "pkg", required = false) String pkg) {
        Integer changeCount = this.dflRequestScansBiz.updateScanController(pkg, getCurrentUserId());
        return ResultData.success(changeCount);
    }

    /**
     * 所有接口方法（通过扫码所有controller接口） 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-10 22:12:23
     */
    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData edit(DflRequestScansPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflRequestScansBiz.saveDflRequestScans(entity);
        return ResultData.success();
    }

    /**
     * 所有接口方法（通过扫码所有controller接口） 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-10 22:12:23
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData update(DflRequestScansPo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflRequestScansBiz.updateDflRequestScans(entity);
        return ResultData.success(v);
    }

    /**
     * 所有接口方法（通过扫码所有controller接口） 删除
     *
     * @param request
     * @param dflRequestScans
     * @return ResultData
     * @author chenjh
     * @date 2022-5-10 22:12:23
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData delete(DflRequestScansPo dflRequestScans, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflRequestScansBiz.deleteDflRequestScans(dflRequestScans.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}