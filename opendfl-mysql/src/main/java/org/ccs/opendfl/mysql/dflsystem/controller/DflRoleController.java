package org.ccs.opendfl.mysql.dflsystem.controller;


import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflRoleBiz;
import org.ccs.opendfl.mysql.dflsystem.po.DflRolePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * DflRolecontroller
 * 角色表 Controller
 *
 * @author chenjh
 * @date 2022-5-3 20:25:42
 */
@RestController
@RequestMapping("/dflSystem/dflRole")
public class DflRoleController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflRoleController.class);

    @Autowired
    private IDflRoleBiz dflRoleBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/mysql/dflRole";
    }

    /**
     * 角色表列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo<> 翻页结果
     * @author chenjh
     * @date 2022-5-3 20:25:42
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public MyPageInfo<DflRolePo> queryPage(HttpServletRequest request, DflRolePo entity, MyPageInfo<DflRolePo> pageInfo) {
        if (entity == null) {
            entity = new DflRolePo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflRoleBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public PageVO<DflRolePo> findByPage(HttpServletRequest request, DflRolePo entity, MyPageInfo<DflRolePo> pageInfo) {
        logger.debug("-------findByPage-------");
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    @RequestMapping(value = {"name/list"})
    @ResponseStatus(value = HttpStatus.OK)

    public List<DflRolePo> findNameByPage(HttpServletRequest request, DflRolePo user) {
        logger.info("-------findNameByPage-------");
        MyPageInfo<DflRolePo> pageInfo = new MyPageInfo<>();
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(100);
        pageInfo = queryPage(request, user, pageInfo);
        return pageInfo.getList();
    }

    /**
     * 角色表 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:25:42
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData edit(DflRolePo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflRoleBiz.saveDflRole(entity);
        return ResultData.success();
    }

    /**
     * 角色表 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:25:42
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData update(DflRolePo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflRoleBiz.updateDflRole(entity);
        return ResultData.success(v);
    }

    /**
     * 角色表 删除
     *
     * @param request
     * @param dflRole
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:25:42
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData delete(DflRolePo dflRole, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflRoleBiz.deleteDflRole(Integer.parseInt(id), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}