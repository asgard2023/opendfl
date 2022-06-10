package org.ccs.opendfl.mysql.dflcore.biz.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.ccs.opendfl.core.constants.CacheTimeType;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.constant.CommonIf;
import org.ccs.opendfl.mysql.dflcore.biz.IDflFrequencyBiz;
import org.ccs.opendfl.mysql.dflcore.mapper.DflFrequencyMapper;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflRequestScansBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 频率限制配置表 业务实现
 * @Title: DflFrequencyBiz
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:43:11
 */
@Service(value = "dflFrequencyBiz")
public class DflFrequencyBiz extends BaseService<DflFrequencyPo> implements IDflFrequencyBiz, ISelfInject {
    @Autowired
    private DflFrequencyMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflFrequencyBiz.class);

    @Override
    public Mapper<DflFrequencyPo> getMapper() {
        return mapper;
    }

    private IDflFrequencyBiz _self;
    @Autowired
    private IDflRequestScansBiz dflRequestScansBiz;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflFrequencyBiz) o;
    }

    @Override
    public Example createConditions(DflFrequencyPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflFrequencyPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
        this.addEqualByKey(criteria, "uri", otherParams);
    }

    @Override
    public MyPageInfo<DflFrequencyPo> findPageBy(DflFrequencyPo entity, MyPageInfo<DflFrequencyPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflFrequencyPo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflFrequencyPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Map<Integer, DflFrequencyPo> getFrequencyByIds(List<Integer> freqencyIdList){
        if (CollectionUtils.isEmpty(freqencyIdList)) {
            return Collections.emptyMap();
        }
        Example example = new Example(DflFrequencyPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        criteria.andIn("id", freqencyIdList);
        List<DflFrequencyPo> uriList = this.mapper.selectByExample(example);
        Map<Integer, DflFrequencyPo> frequencyPoMap = new HashMap<>();
        for (DflFrequencyPo scansPo : uriList) {
            frequencyPoMap.put(scansPo.getId(), scansPo);
        }
        return frequencyPoMap;
    }

    @Override
    public Integer saveDflFrequency(DflFrequencyPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(CommonIf.NO.getType());
        }
        entity.setUriId(this.dflRequestScansBiz.getUriId(entity.getUri()));
        int v = this.mapper.insert(entity);
        this._self.getFrequencyByUri_evict(entity.getUri());
        this._self.getFrequencyByUriMaxUpdateTime_evict(entity.getUri());
        this._self.getFrequencyMaxUpdateTime_evict();
        return v;
    }

    @Override
    public Integer updateDflFrequency(DflFrequencyPo entity) {
        DflFrequencyPo exist = this.findById(entity.getId());
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        if (v > 0) {
            this._self.getFrequencyByCode_evict(exist.getCode(), exist.getTime());
            this._self.getFrequencyByUri_evict(exist.getUri());
            this._self.getFrequencyByUriMaxUpdateTime_evict(exist.getUri());
            this._self.getFrequencyMaxUpdateTime_evict();
        }
        return v;
    }

    @Override
    public Integer deleteDflFrequency(Integer id, Integer operUser, String remark) {
        DflFrequencyPo po = new DflFrequencyPo();
        po.setId(id);
        po.setIfDel(CommonIf.YES.getType()); // 0未删除,1已删除
        po.setModifyUser(operUser);
//        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        if (v > 0) {
            DflFrequencyPo exist = this.findById(id);
            this._self.getFrequencyByCode_evict(exist.getCode(), exist.getTime());
            this._self.getFrequencyByUri_evict(exist.getUri());
            this._self.getFrequencyByUriMaxUpdateTime_evict(exist.getUri());
            this._self.getFrequencyMaxUpdateTime_evict();
        }
        return v;
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE30S, key = "'opendfl:getFrequencyByCode:'+#time+':'+#code")
    public void getFrequencyByCode_evict(String code, Integer time) {
        logger.info("-----getFrequencyByCode_evict--code={} time={}", code, time);
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:getFrequencyByCode:'+#time+':'+#code")
    public DflFrequencyPo getFrequencyByCode(String code, Integer time) {
        ValidateUtils.notNull(code, "code is null");
        ValidateUtils.notNull(time, "time is null");
        DflFrequencyPo search = new DflFrequencyPo();
        search.setCode(code);
        search.setTime(time);
        search.setIfDel(CommonIf.NO.getType());
        return this.mapper.selectOne(search);
    }

    public static final Cache<String, Integer> frequencyIdMap = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS)
            .maximumSize(2000).build();

    @Override
    public Integer getFrequencyIdByCode(String code, Integer time){
        if(code==null||time==null){
            return 0;
        }
        String key=code+":"+time;
        Integer id=frequencyIdMap.getIfPresent(key);
        if(id==null){
            DflFrequencyPo frequencyPo= this._self.getFrequencyByCode(code, time);
            if(frequencyPo!=null){
                id=frequencyPo.getId();
            }
            if(id==null){
                id=-1;
            }
            frequencyIdMap.put(key, id);
        }
        return id;
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE30S, key = "'opendfl:getFrequencyByUri:'+#uri")
    public void getFrequencyByUri_evict(String uri) {
        logger.info("-----getFrequencyByUri_evict--uri={}", uri);
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:getFrequencyByUri:'+#uri")
    public List<DflFrequencyPo> getFrequencyByUri(String uri) {
        ValidateUtils.notNull(uri, "uri is null");
        DflFrequencyPo search = new DflFrequencyPo();
        search.setUri(uri);
        search.setIfDel(CommonIf.NO.getType());
        return this.mapper.select(search);
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE30S, key = "'opendfl:getFrequencyByUriMaxUpTime:'+#uri")
    public void getFrequencyByUriMaxUpdateTime_evict(String uri) {
        logger.info("-----getFrequencyByUriMaxUpdateTime_evict--uri={}", uri);
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:getFrequencyByUriMaxUpTime:'+#uri")
    public Long getFrequencyByUriMaxUpdateTime(String uri) {
        Date maxUpdateTime = this.mapper.getFrequencyByUriMaxUpdateTime(uri);
        if (maxUpdateTime != null) {
            return maxUpdateTime.getTime();
        }
        return null;
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE1H, key = "'opendfl:getFrequencyMaxUpdateTime'")
    public void getFrequencyMaxUpdateTime_evict() {
        logger.info("-----getFrequencyMaxUpdateTime_evict");
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE1H, key = "'opendfl:getFrequencyMaxUpdateTime'")
    public Long getFrequencyMaxUpdateTime() {
        Date maxUpdateTime = this.mapper.getFrequencyMaxUpdateTime();
        if (maxUpdateTime != null) {
            return maxUpdateTime.getTime();
        }
        return null;
    }

    @Override
    public List<DflFrequencyPo> findFrequencyByNewlyModify(Long modifyTime) {
        Example example = new Example(DflFrequencyPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        criteria.andIsNotNull("time");
        criteria.andGreaterThan("modifyTime", new Date(modifyTime));
        return this.mapper.selectByExample(example);
    }
}