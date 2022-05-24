package org.ccs.opendfl.mysql.dflsystem.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflServiceBiz;
import org.ccs.opendfl.mysql.dflsystem.po.DflServicePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Version V1.0
 * @Title: DflServicecontroller
 * @Description: 服务表 Controller
 * @Author: Created by chenjh
 * @Date: 2022-5-4 11:19:02
 */
@RestController
@RequestMapping("/dflSystem/dflService")
public class DflServiceController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflServiceController.class);

    @Autowired
    private IDflServiceBiz dflServiceBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/mysql/dflService";
    }

    /**
     * 服务表列表查询
     *
     * @param request
     * @param entity
     * @param pageInfo
     * @return java.lang.Object
     * @author chenjh
     * @date 2022-5-4 11:19:02
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public MyPageInfo<DflServicePo> queryPage(HttpServletRequest request, DflServicePo entity, MyPageInfo<DflServicePo> pageInfo) {
        if (entity == null) {
            entity = new DflServicePo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflServiceBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    public PageVO<DflServicePo> queryPage2(HttpServletRequest request, DflServicePo entity, MyPageInfo<DflServicePo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 服务表 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-4 11:19:02
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData edit(DflServicePo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflServiceBiz.saveDflService(entity);
        return ResultData.success();
    }

    /**
     * 服务表 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-4 11:19:02
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(DflServicePo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflServiceBiz.updateDflService(entity);
        return ResultData.success(v);
    }

    /**
     * 服务表 删除
     *
     * @param request
     * @param dflService
     * @return ResultData
     * @author chenjh
     * @date 2022-5-4 11:19:02
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ResultData delete(DflServicePo dflService, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflServiceBiz.deleteDflService(dflService.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}