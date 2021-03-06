package org.ccs.opendfl.mysql.dflcore.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflcore.biz.IDflFrequencyBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Version V1.0
 * @Title: DflFrequencycontroller
 * @Description: 频率限制配置表 Controller
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:43:11
 */
@RestController
@RequestMapping("/dflcore/dflFrequency")
public class DflFrequencyController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflFrequencyController.class);

    @Autowired
    private IDflFrequencyBiz dflFrequencyBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "dflcore/mysql/dflFrequency";
    }

    /**
     * 频率限制配置表列表查询
     *
     * @param request
     * @param entity
     * @param pageInfo
     * @return java.lang.Object
     * @author chenjh
     * @date 2022-5-18 21:43:11
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflFrequencyPo> queryPage(HttpServletRequest request, DflFrequencyPo entity, MyPageInfo<DflFrequencyPo> pageInfo) {
        if (entity == null) {
            entity = new DflFrequencyPo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflFrequencyBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }

    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflFrequencyPo> queryPage2(HttpServletRequest request, DflFrequencyPo entity, MyPageInfo<DflFrequencyPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 频率限制配置表 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:43:11
     */
    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData edit(DflFrequencyPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflFrequencyBiz.saveDflFrequency(entity);
        return ResultData.success();
    }

    /**
     * 频率限制配置表 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:43:11
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData update(DflFrequencyPo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflFrequencyBiz.updateDflFrequency(entity);
        return ResultData.success(v);
    }

    /**
     * 频率限制配置表 删除
     *
     * @param request
     * @param dflFrequency
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:43:11
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData delete(DflFrequencyPo dflFrequency, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflFrequencyBiz.deleteDflFrequency(dflFrequency.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}