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
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserBiz;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserRoleBiz;
import org.ccs.opendfl.mysql.dflsystem.po.DflRolePo;
import org.ccs.opendfl.mysql.dflsystem.po.DflUserRolePo;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DflUserRolecontroller
 * 用户角色 Controller
 *
 * @author chenjh
 * @date 2022-5-3 20:26:31
 */
@RestController
@RequestMapping("/dflSystem/dflUserRole")
public class DflUserRoleController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflUserRoleController.class);

    @Autowired
    private IDflUserRoleBiz dflUserRoleBiz;
    @Autowired
    private IDflUserBiz dflUserBiz;
    @Autowired
    private IDflRoleBiz dflRoleBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "/dflUserRole";
    }

    /**
     * 用户角色列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 翻页结果
     * @date 2022-5-3 20:26:31
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public MyPageInfo<DflUserRolePo> queryPage(HttpServletRequest request, DflUserRolePo entity, MyPageInfo<DflUserRolePo> pageInfo) {
        if (entity == null) {
            entity = new DflUserRolePo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflUserRoleBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        List<DflUserRolePo> list = pageInfo.getList();
        List<Integer> roleIdList = list.stream().map(DflUserRolePo::getRoleId).collect(Collectors.toList());
        List<Integer> userIdList = list.stream().map(DflUserRolePo::getUserId).collect(Collectors.toList());
        Map<Integer, DflRolePo> roleMap = this.dflRoleBiz.getRoleMapByIds(roleIdList);
        Map<Integer, UserVo> userVoMap = this.dflUserBiz.getUserMapByIds(userIdList);
        list.forEach(t -> {
            t.setRole(roleMap.get(t.getRoleId()));
            t.setUser(userVoMap.get(t.getUserId()));
        });
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public PageVO<DflUserRolePo> findByPage(HttpServletRequest request, DflUserRolePo entity, MyPageInfo<DflUserRolePo> pageInfo) {
        logger.debug("-------findByPage-------");
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 用户角色 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:26:31
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData edit(DflUserRolePo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflUserRoleBiz.saveDflUserRole(entity);
        return ResultData.success();
    }

    /**
     * 用户角色 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:26:31
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData update(DflUserRolePo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflUserRoleBiz.updateDflUserRole(entity);
        return ResultData.success(v);
    }

    /**
     * 用户角色 删除
     *
     * @param request
     * @param dflUserRole
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:26:31
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData delete(DflUserRolePo dflUserRole, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflUserRoleBiz.deleteDflUserRole(Integer.parseInt(id), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}