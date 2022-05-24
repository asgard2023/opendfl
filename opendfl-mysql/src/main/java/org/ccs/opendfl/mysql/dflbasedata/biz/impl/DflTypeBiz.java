package org.ccs.opendfl.mysql.dflbasedata.biz.impl;

import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dflbasedata.biz.IDflTypeBiz;
import org.ccs.opendfl.mysql.dflbasedata.mapper.DflTypeMapper;
import org.ccs.opendfl.mysql.dflbasedata.po.DflTypePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;

/**
 * @Version V1.0
 * @Title: DflTypeBiz
 * @Description: 业务实现
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:31:07
 */
@Service(value = "dflTypeBiz")
public class DflTypeBiz extends BaseService<DflTypePo> implements IDflTypeBiz, ISelfInject {
    @Autowired
    private DflTypeMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflTypeBiz.class);

    @Override
    public Mapper<DflTypePo> getMapper() {
        return mapper;
    }

    private IDflTypeBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflTypeBiz) o;
    }

    @Override
    public Example createConditions(DflTypePo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflTypePo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
        this.addEqualByKey(criteria, "status", otherParams);
        this.addEqualByKey(criteria, "code", otherParams);
        this.addEqualByKey(criteria, "name", otherParams);
    }

    @Override
    public MyPageInfo<DflTypePo> findPageBy(DflTypePo entity, MyPageInfo<DflTypePo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflTypePo();
        }
        Example example = createConditions(entity, otherParams);
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflTypePo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Map<Integer, DflTypePo> getTypeMapByIds(List<Integer> typeIds) {
        if (CollectionUtils.isEmpty(typeIds)) {
            return Collections.emptyMap();
        }
        Example example = new Example(DflTypePo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", typeIds);
        List<DflTypePo> typeList = this.mapper.selectByExample(example);
        Map<Integer, DflTypePo> typeMap = new HashMap<>(typeList.size());
        for (DflTypePo rolePo : typeList) {
            typeMap.put(rolePo.getId(), rolePo);
        }
        return typeMap;
    }

    @Override
    public List<DflTypePo> getTypeMapByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyList();
        }
        Example example = new Example(DflTypePo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", codes);
        criteria.andEqualTo("status", 1);
        criteria.andEqualTo("ifDel", 0);
        return this.mapper.selectByExample(example);
    }

    @Override
    public Integer saveDflType(DflTypePo entity) {
        if(entity.getStatus()==null){
            entity.setStatus(1);
        }
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
    public Integer updateDflType(DflTypePo entity) {
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflType(Integer id, Integer operUser, String remark) {
        DflTypePo po = new DflTypePo();
        po.setId(id);
        po.setIfDel(1); // 0未删除,1已删除
        po.setModifyUser(operUser);
        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }
}