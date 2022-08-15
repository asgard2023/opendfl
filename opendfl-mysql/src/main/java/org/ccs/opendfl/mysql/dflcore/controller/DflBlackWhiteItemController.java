package org.ccs.opendfl.mysql.dflcore.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflcore.biz.IDflBlackWhiteItemBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflBlackWhiteItemPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * DflBlackWhiteItemcontroller
 * 黑名单 Controller
 *
 * @author chenjh
 * @date 2022-5-18 21:45:02
 */
@RestController
@RequestMapping("/dflcore/dflBlackWhiteItem")
public class DflBlackWhiteItemController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflBlackWhiteItemController.class);

    @Autowired
    private IDflBlackWhiteItemBiz dflBlackWhiteItemBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "dflcore/mysql/dflBlackWhiteItem";
    }

    /**
     * 黑名单列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo<> 翻页结果
     * @author chenjh
     * @date 2022-5-18 21:45:02
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflBlackWhiteItemPo> queryPage(HttpServletRequest request, DflBlackWhiteItemPo entity, MyPageInfo<DflBlackWhiteItemPo> pageInfo) {
        if (entity == null) {
            entity = new DflBlackWhiteItemPo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflBlackWhiteItemBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflBlackWhiteItemPo> queryPage2(HttpServletRequest request, DflBlackWhiteItemPo entity, MyPageInfo<DflBlackWhiteItemPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 黑名单 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:45:02
     */
    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData edit(DflBlackWhiteItemPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflBlackWhiteItemBiz.saveDflBlackWhiteItem(entity);
        return ResultData.success();
    }

    /**
     * 黑名单 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:45:02
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData update(DflBlackWhiteItemPo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflBlackWhiteItemBiz.updateDflBlackWhiteItem(entity);
        return ResultData.success(v);
    }

    /**
     * 黑名单 删除
     *
     * @param request
     * @param dflBlackWhiteItem
     * @return ResultData
     * @author chenjh
     * @date 2022-5-18 21:45:02
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public ResultData delete(DflBlackWhiteItemPo dflBlackWhiteItem, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflBlackWhiteItemBiz.deleteDflBlackWhiteItem(dflBlackWhiteItem.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}