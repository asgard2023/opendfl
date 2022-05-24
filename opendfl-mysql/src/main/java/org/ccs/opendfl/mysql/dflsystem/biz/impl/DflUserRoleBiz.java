package org.ccs.opendfl.mysql.dflsystem.biz.impl;

import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflRoleBiz;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserRoleBiz;
import org.ccs.opendfl.mysql.dflsystem.mapper.DflUserRoleMapper;
import org.ccs.opendfl.mysql.dflsystem.po.DflUserRolePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Version V1.0
 * @Title: DflUserRoleBiz
 * @Description: 用户角色 业务实现
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:26:31
 */
@Service(value = "dflUserRoleBiz")
public class DflUserRoleBiz extends BaseService<DflUserRolePo> implements IDflUserRoleBiz, ISelfInject {
    @Autowired
    private DflUserRoleMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflUserRoleBiz.class);

    @Autowired
    private IDflRoleBiz dflRoleBiz;
    ;

    @Override
    public Mapper<DflUserRolePo> getMapper() {
        return mapper;
    }

    private IDflUserRoleBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflUserRoleBiz) o;
    }

    @Override
    public Example createConditions(DflUserRolePo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        Object keywords = otherParams.get("keywords");
        return example;
    }

    private void searchCondition(DflUserRolePo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
    }

    @Override
    public MyPageInfo<DflUserRolePo> findPageBy(DflUserRolePo entity, MyPageInfo<DflUserRolePo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflUserRolePo();
        }
        Example example = createConditions(entity, otherParams);
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflUserRolePo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public List<DflUserRolePo> findUserRoles(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        Date now = new Date();
        Example example = new Example(DflUserRolePo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("ifDel", 0);
        criteria.andLessThanOrEqualTo("startTime", now);
        criteria.andGreaterThan("endTime", now);
        List<DflUserRolePo> list = this.getMapper().selectByExample(example);
        for (DflUserRolePo entity : list) {
            entity.setRole(dflRoleBiz.findById(entity.getRoleId()));
        }
        return list;
    }

    @Override
    public Integer saveDflUserRole(DflUserRolePo entity) {
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
    public Integer updateDflUserRole(DflUserRolePo entity) {
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflUserRole(Integer id, Integer operUser, String remark) {
        DflUserRolePo po = new DflUserRolePo();
        po.setId(id);
        po.setIfDel(1); // 0未删除,1已删除
        po.setModifyUser(operUser);
//		po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }
}