package org.ccs.opendfl.mysql.dflsystem.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckAuthorization;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflSystemConfigBiz;
import org.ccs.opendfl.mysql.dflsystem.po.DflSystemConfigPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DflSystemConfigcontroller
 * 系统参数配置(树形结构) Controller
 *
 * @author chenjh
 * @date 2022-5-3 20:27:48
 */
@RestController
@RequestMapping("/dflSystem/dflSystemConfig")
public class DflSystemConfigController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflSystemConfigController.class);

    @Autowired
    private IDflSystemConfigBiz dflSystemConfigBiz;

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/mysql/dflSystemConfig";
    }

    /**
     * 系统参数配置(树形结构)列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 翻页结果
     * @date 2022-5-3 20:27:48
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public MyPageInfo<DflSystemConfigPo> queryPage(HttpServletRequest request, DflSystemConfigPo entity, MyPageInfo<DflSystemConfigPo> pageInfo) {
        if (entity == null) {
            entity = new DflSystemConfigPo();
        }
        entity.setIfDel(0);
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflSystemConfigBiz.findPageBy(entity, pageInfo, this.createAllParams(request));
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckAuthorization("admin")
    public PageVO<DflSystemConfigPo> findByPage(HttpServletRequest request, DflSystemConfigPo entity, MyPageInfo<DflSystemConfigPo> pageInfo) {
        logger.debug("-------findByPage-------");
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public ResultData view(DflSystemConfigPo sysconfig) {
        ValidateUtils.notNull(sysconfig.getId(), "id is null");
        DflSystemConfigPo data = dflSystemConfigBiz.selectByKey(sysconfig.getId());
        return ResultData.success(data);
    }

    @RequestMapping(value = "getSysconfigByName")
    public ResultData getSysconfigByName(Integer confType, String name) {
        ValidateUtils.notNull(name, "name is null");

        List<DflSystemConfigPo> list = this.dflSystemConfigBiz.getSysconfigByName(confType, name);
        return ResultData.success(list);
    }

    @RequestMapping(value = {"findSysconfigByParentId"})
//    @CheckAuthorization("admin")
    public List<HashMap<String, Object>> findSysconfigByParentId(Integer confType, Integer parentId) {
        logger.info("-------findSysconfigByParentId--parentId={}", parentId);
        if (parentId == null) {
            parentId = 0;
        }
        List<Integer> idList = Arrays.asList(parentId);
        List<DflSystemConfigPo> list = this.dflSystemConfigBiz.findSysconfigByParentIds(confType, idList);
        idList = list.stream().map(DflSystemConfigPo::getId).collect(Collectors.toList());
        //用于检查是否有子节点
        List<Map<String, Object>> mCountMapList = this.dflSystemConfigBiz.findSysconfigByParentIdsCount(confType, idList);
        List<HashMap<String, Object>> mapList = new ArrayList();
        HashMap<String, Object> map = null;
        boolean hasChild = false;
        Integer pId = null;
        Integer pCount = null;
        for (DflSystemConfigPo sysconfig : list) {
            map = new HashMap<>(8);
            map.put("id", sysconfig.getId());
            map.put("parentId", parentId);
            map.put("text", sysconfig.getName());
            map.put("code", sysconfig.getCode());
            //检查是否有子节点
            hasChild = false;
            for (Map<String, Object> mCountMap : mCountMapList) {
                pId = getIntValue(mCountMap.get("parentId"));
                pCount = getIntValue(mCountMap.get("cout"));
                if (pId.longValue() == sysconfig.getId().intValue() && pCount > 0) {
                    hasChild = true;
                    break;
                }
            }
            if (hasChild) {
                map.put("state", "closed");
            }
            mapList.add(map);
        }
        return mapList;
    }

    private Integer getIntValue(Object obj) {
        if (obj == null) {
            return 0;
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else {
            return Integer.valueOf("" + obj);
        }
    }

    /**
     * 系统参数配置(树形结构) 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:27:48
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData edit(DflSystemConfigPo entity, HttpServletRequest request) {
        if (entity.getId() != null && entity.getId() > 0) {
            return update(entity, request);
        }
        entity.setModifyUser(getCurrentUserId());
        entity.setCreateUser(getCurrentUserId());
        dflSystemConfigBiz.saveDflSystemConfig(entity);
        return ResultData.success(entity.getId());
    }

    /**
     * 系统参数配置(树形结构) 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:27:48
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData update(DflSystemConfigPo entity, HttpServletRequest request) {
        entity.setModifyUser(getCurrentUserId());
        int v = dflSystemConfigBiz.updateDflSystemConfig(entity);
        return ResultData.success(v);
    }

    /**
     * 系统参数配置(树形结构) 删除
     *
     * @param request
     * @param dflSystemConfig
     * @return ResultData
     * @author chenjh
     * @date 2022-5-3 20:27:48
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
    @CheckLogin
    @CheckAuthorization("admin")
    public ResultData delete(DflSystemConfigPo dflSystemConfig, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflSystemConfigBiz.deleteDflSystemConfig(Integer.parseInt(id), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}