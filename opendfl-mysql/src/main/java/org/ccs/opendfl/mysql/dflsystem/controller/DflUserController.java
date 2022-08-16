package org.ccs.opendfl.mysql.dflsystem.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserBiz;
import org.ccs.opendfl.mysql.dflsystem.po.DflUserPo;
import org.ccs.opendfl.mysql.dflsystem.po.UserPo;
import org.ccs.opendfl.mysql.vo.UserVo;
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
import java.util.stream.Collectors;

/**
 * DflUsercontroller
 *
 * @author chenjh
 * @date 2022-5-3 20:24:48
 */
@RestController
@RequestMapping("/dflSystem/dflUser")
public class DflUserController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflUserController.class);

    @Autowired
    private IDflUserBiz dflUserBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/mysql/dflUser";
    }

    /**
     * 列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 翻页结果
     * @date 2022-5-3 20:24:48
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflUserPo> queryPage(HttpServletRequest request, DflUserPo entity, MyPageInfo<DflUserPo> pageInfo) {
        if (entity == null) {
            entity = new DflUserPo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflUserBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflUserPo> findByPage(HttpServletRequest request, DflUserPo entity, MyPageInfo<DflUserPo> pageInfo) {
        logger.debug("-------findByPage-------");
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    @RequestMapping(value = {"name/list"})
    @ResponseStatus(value = HttpStatus.OK)
    public List<UserVo> findNameByPage(HttpServletRequest request, DflUserPo user) {
        logger.info("-------findNameByPage-------");
        MyPageInfo<DflUserPo> pageInfo = new MyPageInfo<>();
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(100);
        pageInfo = queryPage(request, user, pageInfo);
        List<UserVo> voList = pageInfo.getList().stream().map(t -> DflUserPo.toUserVo(t)).collect(Collectors.toList());
        return voList;
    }


    @RequestMapping(value = "/findUserId", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public ResultData findUserId(UserPo entity, HttpServletRequest request) {
        DflUserPo userPo = dflUserBiz.findById(entity.getId());
        return ResultData.success(userPo);
    }

    /**
     * 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:24:48
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData edit(DflUserPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflUserBiz.saveDflUser(entity);
        return ResultData.success();
    }

    /**
     * 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:24:48
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData update(DflUserPo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflUserBiz.updateDflUser(entity);
        return ResultData.success(v);
    }

    /**
     * 删除
     *
     * @param request
     * @param dflUser
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:24:48
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData delete(DflUserPo dflUser, HttpServletRequest request) {
        logger.info("----delete id={}/{}", dflUser.getId(), request.getParameter("id"));
        ValidateUtils.notNull(dflUser.getId(), "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflUserBiz.deleteDflUser(dflUser.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }

    /**
     * 删除
     *
     * @param request
     * @param dflUser
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:24:48
     */

    @RequestMapping(value = "/changePassword", method = {RequestMethod.POST})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData changePassword(DflUserPo dflUser, HttpServletRequest request) {
        logger.info("----changePassword id={}", dflUser.getId());
        ValidateUtils.notNull(dflUser.getId(), "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflUserBiz.changePasswordMgr(dflUser.getId(), dflUser.getPwd(), remark, this.getCurrentUserId());
        return ResultData.success(v);
    }
}