package org.ccs.opendfl.mysql.dflbasedata.controller;

import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflbasedata.biz.IDflTypeBiz;
import org.ccs.opendfl.mysql.dflbasedata.biz.IDflTypeItemBiz;
import org.ccs.opendfl.mysql.dflbasedata.po.DflTypeItemPo;
import org.ccs.opendfl.mysql.dflbasedata.po.DflTypePo;
import org.ccs.opendfl.mysql.vo.TypeItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DflTypeItemcontroller
 *
 * @author chenjh
 * @date 2022-5-3 20:31:25
 */
@RestController
@RequestMapping("/dflBasedata/dflTypeItem")
public class DflTypeItemController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflTypeItemController.class);

    @Autowired
    private IDflTypeItemBiz dflTypeItemBiz;
    @Autowired
    private IDflTypeBiz dflTypeBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "basedata/dflTypeItem";
    }

    /**
     * 列表查询
     *
     * @param request  请求
     * @param entity   对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo<> 翻页结果
     * @date 2022-5-3 20:31:25
     */
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflTypeItemPo> queryPage(HttpServletRequest request, DflTypeItemPo entity, MyPageInfo<DflTypeItemPo> pageInfo) {
        if (entity == null) {
            entity = new DflTypeItemPo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflTypeItemBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        List<DflTypeItemPo> list = pageInfo.getList();
        List<Integer> typeIdList = list.stream().map(DflTypeItemPo::getTypeId).collect(Collectors.toList());
        Map<Integer, DflTypePo> typePoMap = dflTypeBiz.getTypeMapByIds(typeIdList);
        list.forEach(t -> {
            t.setType(typePoMap.get(t.getTypeId()));
        });
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflTypeItemPo> findByPage(HttpServletRequest request, DflTypeItemPo entity, MyPageInfo<DflTypeItemPo> pageInfo) {
        logger.debug("-------findByPage-------");
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }


    @RequestMapping(value = "/typeItems")
    public Map<String, List<TypeItemVo>> getTypeItems(@RequestParam("codes") String codes, @RequestParam(value = "lang", defaultValue = "zh") String lang, HttpServletRequest request) throws BaseException {
        return this.dflTypeItemBiz.getItemsByTypes(lang, codes);
    }


    @RequestMapping(value = "/typeItem")
    public List<TypeItemVo> getTypeItem(@RequestParam("code") String code, @RequestParam(value = "lang", defaultValue = "zh") String lang, HttpServletRequest request) throws BaseException {
        return this.dflTypeItemBiz.getItemsByType(lang, code);
    }

    /**
     * 新增
     *
     * @param entity  对象
     * @param request 请求
     * @return ResultData
     * @date 2022-5-3 20:31:25
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public ResultData edit(DflTypeItemPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflTypeItemBiz.saveDflTypeItem(entity);
        return ResultData.success();
    }

    /**
     * 更新
     *
     * @param request 请求
     * @param entity  对象
     * @return ResultData
     * @date 2022-5-3 20:31:25
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public ResultData update(DflTypeItemPo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflTypeItemBiz.updateDflTypeItem(entity);
        return ResultData.success(v);
    }

    /**
     * 删除
     *
     * @param request     请求
     * @param dflTypeItem 对象
     * @return ResultData
     * @date 2022-5-3 20:31:25
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    @CheckLogin
    public ResultData delete(DflTypeItemPo dflTypeItem, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflTypeItemBiz.deleteDflTypeItem(Integer.parseInt(id), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}