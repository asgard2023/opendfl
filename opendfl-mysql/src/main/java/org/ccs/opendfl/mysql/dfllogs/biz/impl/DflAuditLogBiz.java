package org.ccs.opendfl.mysql.dfllogs.biz.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflAuditLogBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflRequestScansBiz;
import org.ccs.opendfl.mysql.dfllogs.mapper.DflAuditLogMapper;
import org.ccs.opendfl.mysql.dfllogs.po.DflAuditLogPo;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DflAuditLogBiz
 * 后台管理审计日志 业务实现
 *
 * @author chenjh
 * @date 2022-5-6 23:20:31
 */
@Service(value = "dflAuditLogBiz")
public class DflAuditLogBiz extends BaseService<DflAuditLogPo> implements IDflAuditLogBiz, ISelfInject {
    @Autowired
    private DflAuditLogMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflAuditLogBiz.class);

    @Override
    public Mapper<DflAuditLogPo> getMapper() {
        return mapper;
    }

    private IDflAuditLogBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflAuditLogBiz) o;
    }

    @Autowired
    private IDflUserBiz dflUserBiz;
    @Autowired
    private IDflRequestScansBiz dflRequestScansBiz;

    @Override
    public Example createConditions(DflAuditLogPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflAuditLogPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
        String startTime = (String) otherParams.get("startTime");
        if (StringUtil.isNotEmpty(startTime)) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);
        }
        String endTime = (String) otherParams.get("endTime");
        if (StringUtil.isNotEmpty(endTime)) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }

        this.addEqualByKey(criteria, "id", otherParams);
        this.addEqualByKey(criteria, "ip", otherParams);
        this.addEqualByKey(criteria, "uriId", otherParams);
        this.addEqualByKey(criteria, "uri", otherParams);
        this.addEqualByKey(criteria, "roleId", otherParams);

        String userNickname = (String) otherParams.get("user.nickname");
        if (CharSequenceUtil.isNotBlank(userNickname)) {
            Integer userId = dflUserBiz.getUserIdByNickName(userNickname);
            if (userId == null) {
                userId = -1;
            }
            criteria.andEqualTo("userId", userId);
        }
    }

    @Override
    public MyPageInfo<DflAuditLogPo> findPageBy(DflAuditLogPo entity, MyPageInfo<DflAuditLogPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflAuditLogPo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflAuditLogPo> list = this.getMapper().selectByExample(example);
        list.forEach(t -> {
            if (CharSequenceUtil.isNumeric(t.getIp())) {
                t.setIp(RequestUtils.getNumConvertIp(Long.parseLong(t.getIp())));
            }
        });
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveDflAuditLog(DflAuditLogPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setIp(RequestUtils.convertIpv4(entity.getIp()));
        entity.setUriId(dflRequestScansBiz.getUriId(entity.getUri()));
        //uriId不为空时，uri不用保存
        if (entity.getUriId() != null) {
            entity.setUri(null);
        }
        int v = this.mapper.insert(entity);
        return v;
    }

    @Override
    public Integer updateDflAuditLog(DflAuditLogPo entity) {
        entity.setIp(RequestUtils.convertIpv4(entity.getIp()));
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflAuditLog(Long id, Integer operUser, String remark) {
        DflAuditLogPo po = new DflAuditLogPo();
        po.setId(id);
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }
}