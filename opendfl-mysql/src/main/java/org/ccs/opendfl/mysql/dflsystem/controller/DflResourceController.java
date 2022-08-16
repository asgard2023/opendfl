package org.ccs.opendfl.mysql.dflsystem.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflResourceBiz;
import org.ccs.opendfl.mysql.dflsystem.po.DflResourcePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * DflResourcecontroller
 *
 * @author chenjh
 * @date 2022-5-4 11:09:37
 */
@RestController
@RequestMapping("/dflSystem/dflResource")
public class DflResourceController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflResourceController.class);

    @Autowired
    private IDflResourceBiz dflResourceBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/mysql/dflResource";
    }

    /**
     * 列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 翻页结果
     * @author chenjh
     * @date 2022-5-4 11:09:37
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public MyPageInfo<DflResourcePo> queryPage(HttpServletRequest request, DflResourcePo entity, MyPageInfo<DflResourcePo> pageInfo) {
        if (entity == null) {
            entity = new DflResourcePo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflResourceBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    public PageVO<DflResourcePo> queryPage2(HttpServletRequest request, DflResourcePo entity, MyPageInfo<DflResourcePo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-4 11:09:37
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData edit(DflResourcePo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflResourceBiz.saveDflResource(entity);
        return ResultData.success();
    }

    /**
     * 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-4 11:09:37
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(DflResourcePo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflResourceBiz.updateDflResource(entity);
        return ResultData.success(v);
    }

    /**
     * 删除
     *
     * @param request
     * @param dflResource
     * @return ResultData
     * @author chenjh
     * @date 2022-5-4 11:09:37
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ResultData delete(DflResourcePo dflResource, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflResourceBiz.deleteDflResource(dflResource.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}