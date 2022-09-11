package org.ccs.opendfl.mysql.dflsystem.controller;

import com.github.pagehelper.PageInfo;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.dflsystem.biz.IUserBiz;
import org.ccs.opendfl.mysql.dflsystem.po.UserPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "userInfoBiz")
    private IUserBiz userBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/user";
    }

    /**
     * 列表查询
     *
     * @param request  请求
     * @param entity   对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 翻页结果
     * @date 2022-3-30 21:27:43
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public Object queryPage(HttpServletRequest request, UserPo entity, PageInfo<UserPo> pageInfo) {
        if (entity == null) {
            entity = new UserPo();
        }
        Map<String, Object> params = RequestUtils.getParamsObject(request);
        pageInfo = userBiz.findPageBy(entity, pageInfo, params);
        return pageInfo;
    }

    /**
     * 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author Chenjh
     * @date 2022-3-30 21:27:43
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData save(UserPo entity, HttpServletRequest request) {
        entity.setCreateUser(getCurrentUsername());
        entity.setUpdateUser(getCurrentUsername());
        userBiz.saveUser(entity);
        return ResultData.success();
    }


    @RequestMapping(value = "/findUserId", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData findUserId(UserPo entity, HttpServletRequest request) {
        UserPo userPo = userBiz.findById(entity.getId());
        return ResultData.success(userPo);
    }


    /**
     * 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author Chenjh
     * @date 2022-3-30 21:27:43
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(UserPo entity, HttpServletRequest request) {
        entity.setUpdateUser(getCurrentUsername());
        int v = userBiz.updateUser(entity);
        return ResultData.success(v);
    }

    /**
     * 删除
     *
     * @param request
     * @param id      用户ID
     * @return ResultData
     * @author Chenjh
     * @date 2022-3-30 21:27:43
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData delete(@RequestParam(name = "id", required = false) Integer id, HttpServletRequest request) {
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = userBiz.deleteUser(id, this.getCurrentUsername(), remark);
        return ResultData.success(v);
    }
}