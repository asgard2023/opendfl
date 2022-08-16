package org.ccs.opendfl.mysql.dfllogs.controller;

import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.auth.CheckLogin;
import org.ccs.opendfl.mysql.base.BaseController;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.config.MysqlConfiguration;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflLogUserBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflOutLockLogBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflRequestScansBiz;
import org.ccs.opendfl.mysql.dfllogs.po.DflLogUserPo;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLockLogPo;
import org.ccs.opendfl.mysql.dfllogs.po.DflRequestScansPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DflOutLockLogcontroller
 * 分布式锁超限日志 Controller
 *
 * @author chenjh
 * @date 2022-5-6 23:22:04
 */
@RestController
@RequestMapping("/dflLogs/dflOutLockLog")
public class DflOutLockLogController extends BaseController {

    static Logger logger = LoggerFactory.getLogger(DflOutLockLogController.class);

    @Autowired
    private IDflOutLockLogBiz dflOutLockLogBiz;
    @Autowired
    private IDflRequestScansBiz dflRequestScansBiz;
    @Autowired
    private IDflLogUserBiz dflLogUserBiz;
    @Autowired
    private MysqlConfiguration mysqlConfiguration;


    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "mysql/mysql/dflOutLockLog";
    }

    /**
     * 分布式锁超限日志列表查询
     *
     * @param request 请求
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return MyPageInfo 翻页结果
     * @author chenjh
     * @date 2022-5-6 23:22:04
     */

    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public MyPageInfo<DflOutLockLogPo> queryPage(HttpServletRequest request, DflOutLockLogPo entity, MyPageInfo<DflOutLockLogPo> pageInfo) {
        if (entity == null) {
            entity = new DflOutLockLogPo();
        }
        Map<String, Object> params = this.createAllParams(request);
        ValidateUtils.checkTimeDateLimit(params, "uri,ip,uid,userId", 90);
        if (this.mysqlConfiguration.getUserIdToNum() == 1) {
            if (StringUtils.isNotBlank(entity.getUserId())) {
                entity.setUid(this.dflLogUserBiz.getUid(entity.getUserId(), null, null));
                entity.setUserId(null);
                params.put("uid", entity.getUid());
                params.remove("userId");
            }
        }
        if (StringUtils.isNotBlank(entity.getIp())) {
            entity.setIp(RequestUtils.convertIpv4(entity.getIp()));
            params.put("ip", entity.getIp());
        }
        if (pageInfo.getPageSize() == 0) {
            pageInfo.setPageSize(getPageSize());
        }
        pageInfo = dflOutLockLogBiz.findPageBy(entity, pageInfo, params);
        List<DflOutLockLogPo> list = pageInfo.getList();

        final Map<Long, DflLogUserPo> userMap = new HashMap<>(list.size());
        if (mysqlConfiguration.getUserIdToNum() == 1) {
            List<Long> userIdList = list.stream().filter(t -> t.getUid() != null).map(DflOutLockLogPo::getUid).distinct().collect(Collectors.toList());
            userMap.putAll(dflLogUserBiz.getUserPos(userIdList));
        }

        List<Integer> uriIdList = list.stream().filter(t -> t.getUriId() != null).map(DflOutLockLogPo::getUriId).distinct().collect(Collectors.toList());
        Map<Integer, DflRequestScansPo> uriMap = dflRequestScansBiz.getUriPos(uriIdList);

        list.stream().forEach(t -> {
            t.setUser(userMap.get(t.getUid()));
            t.setUriPo(uriMap.get(t.getUriId()));
            if (t.getUriPo() != null) {
                t.setUri(t.getUriPo().getUri());
            }
            if (StringUtils.isNumeric(t.getIp())) {
                t.setIp(RequestUtils.getNumConvertIp(Long.parseLong(t.getIp())));
            }
        });
        return pageInfo;
    }


    @RequestMapping(value = "/list2", method = {RequestMethod.POST, RequestMethod.GET})
    @CheckLogin
    public PageVO<DflOutLockLogPo> queryPage2(HttpServletRequest request, DflOutLockLogPo entity, MyPageInfo<DflOutLockLogPo> pageInfo) {
        this.pageSortBy(pageInfo);
        pageInfo = queryPage(request, entity, pageInfo);
        return new PageVO(pageInfo);
    }

    /**
     * 分布式锁超限日志 新增
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:22:04
     */

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData edit(DflOutLockLogPo entity, HttpServletRequest request) {
        dflOutLockLogBiz.saveDflOutLockLog(entity);
        return ResultData.success();
    }

    /**
     * 分布式锁超限日志 更新
     *
     * @param request
     * @param entity
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:22:04
     */

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData update(DflOutLockLogPo entity, HttpServletRequest request) {
        int v = dflOutLockLogBiz.updateDflOutLockLog(entity);
        return ResultData.success(v);
    }

    /**
     * 分布式锁超限日志 删除
     *
     * @param request
     * @param dflOutLockLog
     * @return ResultData
     * @author chenjh
     * @date 2022-5-6 23:22:04
     */

    @RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultData delete(DflOutLockLogPo dflOutLockLog, HttpServletRequest request) {
        String id = request.getParameter("id");
        ValidateUtils.notNull(id, "id不能为空");
        String remark = request.getParameter("remark");
        int v = dflOutLockLogBiz.deleteDflOutLockLog(dflOutLockLog.getId(), this.getCurrentUserId(), remark);
        return ResultData.success(v);
    }
}