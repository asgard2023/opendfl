package org.ccs.opendfl.mysql.dflbasedata.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflbasedata.biz.IDflTypeBiz;
import org.ccs.opendfl.mysql.dflbasedata.po.DflTypePo;
import org.ccs.opendfl.mysql.dflsystem.po.DflRolePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * DflTypecontroller
 *
 * @author chenjh
 * @date 2022-5-3 20:31:07
 */
@RestController
@RequestMapping("/dflBasedata/dflType")
public class DflTypeController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflTypeController.class);

    @Autowired
    private IDflTypeBiz dflTypeBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "basedata/dflType";
    }

    /**
     * 列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 翻页结果
     * @author chenjh
     * @date 2022-5-3 20:31:07
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflTypePo> queryPage(HttpServletRequest request, DflTypePo entity, MyPageInfo<DflTypePo> pageInfo) {
        if (entity == null) {
            entity = new DflTypePo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflTypeBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflRolePo> findByPage(HttpServletRequest request, DflTypePo entity, MyPageInfo<DflTypePo> pageInfo) {
        logger.debug("-------findByPage-------");
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
     * @date 2022-5-3 20:31:07
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public ResultData edit(DflTypePo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflTypeBiz.saveDflType(entity);
        return ResultData.success();
    }

    /**
     * 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:31:07
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public ResultData update(DflTypePo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflTypeBiz.updateDflType(entity);
        return ResultData.success(v);
    }

    /**
     * 删除
     *
     * @param request
     * @param dflType
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:31:07
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    @CheckLogin
    public ResultData delete(DflTypePo dflType, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflTypeBiz.deleteDflType(Integer.parseInt(id), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}