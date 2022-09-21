package org.ccs.opendfl.mysql.dflcore.biz.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.ccs.opendfl.core.constants.CacheTimeType;
import org.ccs.opendfl.core.constants.FrequencyType;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.constant.CommonIf;
import org.ccs.opendfl.mysql.constant.CommonStatus;
import org.ccs.opendfl.mysql.dflcore.biz.IDflFrequencyBiz;
import org.ccs.opendfl.mysql.dflcore.mapper.DflFrequencyMapper;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.ccs.opendfl.mysql.dflcore.vo.DflFrequencyConfigCountVo;
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
 * 频率限制配置表 业务实现
 * DflFrequencyBiz
 *
 * @author chenjh
 * @date 2022-5-18 21:43:11
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
        this.addEqualByKey(criteria, "status", otherParams);
        if(CharSequenceUtil.isNotEmpty(entity.getUri())){
            criteria.andLike("uri", entity.getUri()+"%");
        }
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
    public Map<Integer, DflFrequencyPo> getFrequencyByIds(List<Integer> freqencyIdList) {
        if (CollectionUtils.isEmpty(freqencyIdList)) {
            return Collections.emptyMap();
        }
        Example example = new Example(DflFrequencyPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        criteria.andIn("id", freqencyIdList);
        List<DflFrequencyPo> uriList = this.mapper.selectByExample(example);
        Map<Integer, DflFrequencyPo> frequencyPoMap = new HashMap<>(uriList.size());
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
        if(entity.getLog()==null){
            entity.setLog(0);
        }
        if(entity.getNeedLogin()==null){
            entity.setNeedLogin(0);
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
        if (CharSequenceUtil.isEmpty(exist.getLimitType())) {
            entity.setLimitType(FrequencyType.URI_CONFIG.getType());
        }
        if (entity.getTime() == null || entity.getLimitCount() == null) {
            entity.setLimitType(null);
        }
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        if (v > 0) {
            this._self.getFrequencyByCode_evict(exist.getCode(), exist.getFreqLimitType(), exist.getTime());
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
            this._self.getFrequencyByCode_evict(exist.getCode(), exist.getFreqLimitType(), exist.getTime());
            this._self.getFrequencyByUri_evict(exist.getUri());
            this._self.getFrequencyByUriMaxUpdateTime_evict(exist.getUri());
            this._self.getFrequencyMaxUpdateTime_evict();
        }
        return v;
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE30S, key = "'opendfl:getFrequencyByCode:'+#time+':'+#freqLimitType+':'+#code")
    public void getFrequencyByCode_evict(String code, Integer freqLimitType, Integer time) {
        logger.info("-----getFrequencyByCode_evict--code={} freqLimitType={} time={}", code, freqLimitType, time);
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:getFrequencyByCode:'+#time+':'+#freqLimitType+':'+#code")
    public DflFrequencyPo getFrequencyByCode(String code, Integer freqLimitType, Integer time) {
        ValidateUtils.notNull(code, "code is null");
        ValidateUtils.notNull(time, "time is null");
        DflFrequencyPo search = new DflFrequencyPo();
        search.setCode(code);
        search.setTime(time);
        search.setFreqLimitType(freqLimitType);
        search.setIfDel(CommonIf.NO.getType());
        return this.mapper.selectOne(search);
    }

    public static final Cache<String, Integer> frequencyIdMap = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS)
            .maximumSize(2000).build();

    @Override
    public Integer getFrequencyIdByCode(String code, Integer freqLimitType, Integer time) {
        if (code == null || time == null) {
            return 0;
        }
        String key = code + ":" + time;
        Integer id = frequencyIdMap.getIfPresent(key);
        if (id == null) {
            DflFrequencyPo frequencyPo = this._self.getFrequencyByCode(code, freqLimitType, time);
            if (frequencyPo != null) {
                id = frequencyPo.getId();
            }
            if (id == null) {
                id = -1;
            }
            frequencyIdMap.put(key, id);
        }
        return id;
    }

    @Override
    public List<DflFrequencyPo> getFrequencyByUris(List<String> uris, String fields) {
        if(CollectionUtils.isEmpty(uris)){
            return Collections.emptyList();
        }
        return getFrequencyByUrls(uris, fields, CommonStatus.VALID.getStatus());
    }


    private List<DflFrequencyPo> getFrequencyByUrls(List<String> uris, String fields, Integer status) {
        Example example = new Example(DflFrequencyPo.class);
        if(CharSequenceUtil.isNotEmpty(fields)) {
            example.selectProperties(fields.split(","));
        }
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", CommonIf.NO.getType());
        if(status!=null) {
            criteria.andEqualTo("status", status);
        }
        criteria.andIn("uri", uris);
        return this.mapper.selectByExample(example);
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
        return this.getFrequencyByUrls(Arrays.asList(uri), DflFrequencyPo.FREQUENCY_DATA_FIELD, null);
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
        String fields=DflFrequencyPo.FREQUENCY_DATA_FIELD+",modifyTime";
        example.selectProperties(fields.split(","));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        criteria.andIsNotNull("time");
        criteria.andGreaterThan("modifyTime", new Date(modifyTime));
        return this.mapper.selectByExample(example);
    }

    public MyPageInfo<DflFrequencyConfigCountVo> uriConfigCounts(DflFrequencyConfigCountVo entity, MyPageInfo<DflFrequencyConfigCountVo> pageInfo){
        if(StrUtil.isBlank(entity.getUri())){
            entity.setUri(null);
        }
        String orderSql=null;
        if(StrUtil.isBlank(pageInfo.getOrderBy())){
            pageInfo.setOrderBy("cout");
        }
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            orderSql=StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder();
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize()).setOrderBy(orderSql);
        List<DflFrequencyConfigCountVo> list= mapper.uriConfigCounts(entity.getUri());
        return new MyPageInfo<>(list);
    }
}