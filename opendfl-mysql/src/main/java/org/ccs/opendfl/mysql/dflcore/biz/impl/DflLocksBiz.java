package org.ccs.opendfl.mysql.dflcore.biz.impl;

import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.core.constants.CacheTimeType;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dflcore.biz.IDflLocksBiz;
import org.ccs.opendfl.mysql.dflcore.mapper.DflLocksMapper;
import org.ccs.opendfl.mysql.dflcore.po.DflLocksPo;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflRequestScansBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 分布式锁配置表 业务实现
 * @Title: DflLocksBiz
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:44:08
 */
@Service(value = "dflLocksBiz")
public class DflLocksBiz extends BaseService<DflLocksPo> implements IDflLocksBiz, ISelfInject {
    @Autowired
    private DflLocksMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflLocksBiz.class);

    @Override
    public Mapper<DflLocksPo> getMapper() {
        return mapper;
    }

    private IDflLocksBiz _self;
    @Autowired
    private IDflRequestScansBiz dflRequestScansBiz;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflLocksBiz) o;
    }

    @Override
    public Example createConditions(DflLocksPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflLocksPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
    public MyPageInfo<DflLocksPo> findPageBy(DflLocksPo entity, MyPageInfo<DflLocksPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflLocksPo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflLocksPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveDflLocks(DflLocksPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setModifyTime(new Date());
        entity.setUriId(this.dflRequestScansBiz.getUriId(entity.getUri()));
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.mapper.insert(entity);
        return v;
    }

    @Override
    public Integer updateDflLocks(DflLocksPo entity) {
        DflLocksPo exist = this.findById(entity.getId());
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        if (v > 0) {
            this._self.getLockByCode_evict(exist.getName());
        }
        return v;
    }

    @Override
    public Integer deleteDflLocks(Integer id, Integer operUser, String remark) {
        DflLocksPo exist = this.findById(id);
        DflLocksPo po = new DflLocksPo();
        po.setId(id);
        po.setIfDel(1); // 0未删除,1已删除
        po.setModifyUser(operUser);
//        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        if (v > 0) {
            this._self.getLockByCode_evict(exist.getName());
        }
        return v;
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE30S, key = "'opendfl:getLockByCode:'+#code")
    public void getLockByCode_evict(String code) {
        logger.info("-----getLockByCode_evict--code={}", code);
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:getLockByCode:'+#code")
    public DflLocksPo getLockByCode(String code) {
        DflLocksPo search = new DflLocksPo();
        search.setIfDel(0); // 0未删除,1已删除
        search.setCode(code);
        return this.mapper.selectOne(search);
    }
}