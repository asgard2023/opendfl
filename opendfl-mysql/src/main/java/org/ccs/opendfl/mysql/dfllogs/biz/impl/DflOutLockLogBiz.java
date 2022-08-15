package org.ccs.opendfl.mysql.dfllogs.biz.impl;

import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflOutLockLogBiz;
import org.ccs.opendfl.mysql.dfllogs.mapper.DflOutLockLogMapper;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLockLogPo;
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
 * DflOutLockLogBiz
 * 分布式锁超限日志 业务实现
 *
 * @author chenjh
 * @date 2022-5-6 23:22:04
 */
@Service(value = "dflOutLockLogBiz")
public class DflOutLockLogBiz extends BaseService<DflOutLockLogPo> implements IDflOutLockLogBiz, ISelfInject {
    @Autowired
    private DflOutLockLogMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflOutLockLogBiz.class);

    @Override
    public Mapper<DflOutLockLogPo> getMapper() {
        return mapper;
    }

    private IDflOutLockLogBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflOutLockLogBiz) o;
    }

    @Override
    public Example createConditions(DflOutLockLogPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflOutLockLogPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
        String startTime = (String) otherParams.get("startTime");
        if (StringUtil.isNotEmpty(startTime)) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);
        }
        String endTime = (String) otherParams.get("endTime");
        if (StringUtil.isNotEmpty(endTime)) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }

        this.addEqualByKey(criteria, "id", otherParams);
        this.addEqualByKey(criteria, "uid", otherParams);
        this.addEqualByKey(criteria, "uri", otherParams);
        this.addEqualByKey(criteria, "userId", otherParams);
        this.addEqualByKey(criteria, "ip", otherParams);
        this.addEqualByKey(criteria, "attrValue", otherParams);
    }

    @Override
    public MyPageInfo<DflOutLockLogPo> findPageBy(DflOutLockLogPo entity, MyPageInfo<DflOutLockLogPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflOutLockLogPo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflOutLockLogPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveDflOutLockLog(DflOutLockLogPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        int v = this.mapper.insert(entity);
        return v;
    }

    @Override
    public Integer updateDflOutLockLog(DflOutLockLogPo entity) {
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflOutLockLog(Long id, Integer operUser, String remark) {
        DflOutLockLogPo po = new DflOutLockLogPo();
        po.setId(id);
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }
}