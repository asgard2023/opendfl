package org.ccs.opendfl.mysql.dfllogs.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflLogUserBiz;
import org.ccs.opendfl.mysql.dfllogs.po.DflLogUserPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * DflLogUsercontroller
 * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 Controller
 *
 * @author chenjh
 * @date 2022-5-10 22:16:27
 */
@RestController
@RequestMapping("/dflLogUser")
public class DflLogUserController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflLogUserController.class);

    @Autowired
    private IDflLogUserBiz dflLogUserBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/mysql/dflLogUser";
    }

    /**
     * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo<> 翻页结果
     * @author chenjh
     * @date 2022-5-10 22:16:27
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public MyPageInfo<DflLogUserPo> queryPage(HttpServletRequest request, DflLogUserPo entity, MyPageInfo<DflLogUserPo> pageInfo) {
        if (entity == null) {
            entity = new DflLogUserPo();
        }
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflLogUserBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    public PageVO<DflLogUserPo> queryPage2(HttpServletRequest request, DflLogUserPo entity, MyPageInfo<DflLogUserPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-10 22:16:27
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData edit(DflLogUserPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
//		entity.setModifyUser(getCurrentUserId());
//		entity.setCreateUser(getCurrentUserId());
        dflLogUserBiz.saveDflLogUser(entity);
        return ResultData.success();
    }

    /**
     * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-10 22:16:27
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(DflLogUserPo entity, HttpServletRequest request) {
//		entity.setModifyUser(getCurrentUserId());
        int v = dflLogUserBiz.updateDflLogUser(entity);
        return ResultData.success(v);
    }

    /**
     * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 删除
     *
     * @param request
     * @param dflLogUser
     * @return ResultData
     * @author chenjh
     * @date 2022-5-10 22:16:27
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData delete(DflLogUserPo dflLogUser, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflLogUserBiz.deleteDflLogUser(dflLogUser.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}