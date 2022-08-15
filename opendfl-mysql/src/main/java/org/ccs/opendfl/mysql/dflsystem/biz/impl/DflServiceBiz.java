package org.ccs.opendfl.mysql.dflsystem.biz.impl;

import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflServiceBiz;
import org.ccs.opendfl.mysql.dflsystem.mapper.DflServiceMapper;
import org.ccs.opendfl.mysql.dflsystem.po.DflServicePo;
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
 * DflServiceBiz
 * 服务表 业务实现
 *
 * @author chenjh
 * @date 2022-5-4 11:19:02
 */
@Service(value = "dflServiceBiz")
public class DflServiceBiz extends BaseService<DflServicePo> implements IDflServiceBiz, ISelfInject {
    @Autowired
    private DflServiceMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflServiceBiz.class);

    @Override
    public Mapper<DflServicePo> getMapper() {
        return mapper;
    }

    private IDflServiceBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflServiceBiz) o;
    }

    @Override
    public Example createConditions(DflServicePo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflServicePo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
        String startTime = (String) otherParams.get("startTime");
        if (StringUtil.isNotEmpty(startTime)) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);
        }
        String endTime = (String) otherParams.get("endTime");
        if (StringUtil.isNotEmpty(endTime)) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }

        if (entity.getIfDel() != null) {
            criteria.andEqualTo("ifDel", entity.getIfDel());
        }
        this.addEqualByKey(criteria, "id", otherParams);
    }

    @Override
    public MyPageInfo<DflServicePo> findPageBy(DflServicePo entity, MyPageInfo<DflServicePo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflServicePo();
        }
        Example example = createConditions(entity, otherParams);
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflServicePo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveDflService(DflServicePo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.mapper.insert(entity);
        return v;
    }

    @Override
    public Integer updateDflService(DflServicePo entity) {
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflService(Integer id, Integer operUser, String remark) {
        DflServicePo po = new DflServicePo();
        po.setId(id);
        // 0未删除,1已删除
        po.setIfDel(1);
        po.setModifyUser(operUser);
//        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }
}