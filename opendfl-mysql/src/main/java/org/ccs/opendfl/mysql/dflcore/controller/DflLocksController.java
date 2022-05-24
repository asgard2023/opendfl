package org.ccs.opendfl.mysql.dflcore.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflcore.biz.IDflLocksBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflLocksPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Version V1.0
 * @Title: DflLockscontroller
 * @Description: 分布式锁配置表 Controller
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:44:08
 */
@RestController
@RequestMapping("/dflcore/dflLocks")
public class DflLocksController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflLocksController.class);

    @Autowired
    private IDflLocksBiz dflLocksBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "dflcore/mysql/dflLocks";
    }

    /**
     * 分布式锁配置表列表查询
     *
     * @param request
     * @param entity
     * @param pageInfo
     * @return java.lang.Object
     * @author chenjh
     * @date 2022-5-18 21:44:08
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflLocksPo> queryPage(HttpServletRequest request, DflLocksPo entity, MyPageInfo<DflLocksPo> pageInfo) {
        if (entity == null) {
            entity = new DflLocksPo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflLocksBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }

    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflLocksPo> queryPage2(HttpServletRequest request, DflLocksPo entity, MyPageInfo<DflLocksPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 分布式锁配置表 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:44:08
     */
    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData edit(DflLocksPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflLocksBiz.saveDflLocks(entity);
        return ResultData.success();
    }

    /**
     * 分布式锁配置表 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:44:08
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData update(DflLocksPo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflLocksBiz.updateDflLocks(entity);
        return ResultData.success(v);
    }

    /**
     * 分布式锁配置表 删除
     *
     * @param request
     * @param dflLocks
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:44:08
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData delete(DflLocksPo dflLocks, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflLocksBiz.deleteDflLocks(dflLocks.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}