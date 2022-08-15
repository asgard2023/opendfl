package org.ccs.opendfl.mysql.dflcore.biz.impl;

import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.core.constants.CacheTimeType;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dflcore.biz.IDflBlackWhiteBiz;
import org.ccs.opendfl.mysql.dflcore.biz.IDflBlackWhiteItemBiz;
import org.ccs.opendfl.mysql.dflcore.mapper.DflBlackWhiteItemMapper;
import org.ccs.opendfl.mysql.dflcore.po.DflBlackWhiteItemPo;
import org.ccs.opendfl.mysql.dflcore.po.DflBlackWhitePo;
import org.ccs.opendfl.mysql.dflcore.vo.DflBlackWhiteVo;
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
import java.util.stream.Collectors;

/**
 * 黑名单 业务实现
 * DflBlackWhiteItemBiz
 *
 * @author chenjh
 * @date 2022-5-18 21:45:02
 */
@Service(value = "dflBlackWhiteItemBiz")
public class DflBlackWhiteItemBiz extends BaseService<DflBlackWhiteItemPo> implements IDflBlackWhiteItemBiz, ISelfInject {
    @Autowired
    private DflBlackWhiteItemMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflBlackWhiteItemBiz.class);

    @Override
    public Mapper<DflBlackWhiteItemPo> getMapper() {
        return mapper;
    }

    private IDflBlackWhiteItemBiz _self;

    @Autowired
    private IDflBlackWhiteBiz dflBlackWhiteBiz;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflBlackWhiteItemBiz) o;
    }

    @Override
    public Example createConditions(DflBlackWhiteItemPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflBlackWhiteItemPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
        this.addEqualByKey(criteria, "blackwhiteId", otherParams);
    }

    @Override
    public MyPageInfo<DflBlackWhiteItemPo> findPageBy(DflBlackWhiteItemPo entity, MyPageInfo<DflBlackWhiteItemPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflBlackWhiteItemPo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflBlackWhiteItemPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveDflBlackWhiteItem(DflBlackWhiteItemPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.mapper.insert(entity);
        if (v > 0) {
            this.evictCache(entity.getBlackwhiteId());
        }
        return v;
    }

    @Override
    public Integer updateDflBlackWhiteItem(DflBlackWhiteItemPo entity) {
        DflBlackWhiteItemPo exist = this.findById(entity.getId());
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        if (v > 0) {
            this.evictCache(exist.getBlackwhiteId());
        }
        return v;
    }

    @Override
    public Integer deleteDflBlackWhiteItem(Integer id, Integer operUser, String remark) {
        DflBlackWhiteItemPo exist = this.findById(id);
        DflBlackWhiteItemPo po = new DflBlackWhiteItemPo();
        po.setId(id);
        // 0未删除,1已删除
        po.setIfDel(1);
        po.setModifyUser(operUser);
        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        if (v > 0) {
            this.evictCache(exist.getBlackwhiteId());
        }
        return v;
    }

    private void evictCache(Integer blackWhiteId) {
        DflBlackWhitePo type = this.dflBlackWhiteBiz.findById(blackWhiteId);
        this._self.findBlackWhiteList_evict(type.getType(), type.getLimitType());
        this._self.findBlackWhiteMaxUpdateTime_evict(type.getType(), type.getLimitType());
        this._self.findBlackWhiteMaxUpdateTime_evict(type.getType(), null);
    }

    private List<DflBlackWhiteItemPo> getItemByWhiteTypeIds(List<Integer> whiteTypeIds) {
        if (CollectionUtils.isEmpty(whiteTypeIds)) {
            return Collections.emptyList();
        }
        Example example = new Example(DflBlackWhiteItemPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("blackwhiteId", whiteTypeIds);
        criteria.andEqualTo("ifDel", 0);
        criteria.andEqualTo("status", 1);
        return this.mapper.selectByExample(example);
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE30S, key = "'opendfl:findBlackWhiteList:'+#type+':'+#limitType")
    public void findBlackWhiteList_evict(String type, Integer limitType) {
        logger.info("-----findBlackWhiteList_evict--type={} limitType={}", type, limitType);
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:findBlackWhiteList:'+#type+':'+#limitType")
    public List<DflBlackWhiteVo> findBlackWhiteList(String type, Integer limitType) {
        ValidateUtils.notNull(type, "type is null");
        List<DflBlackWhitePo> whiteBlackList = this.dflBlackWhiteBiz.findBlackWhiteByType(type, limitType);
        List<Integer> typeIdList = whiteBlackList.stream().map(DflBlackWhitePo::getId).collect(Collectors.toList());
        List<DflBlackWhiteItemPo> itemList = this.getItemByWhiteTypeIds(typeIdList);
        List<DflBlackWhiteVo> list = new ArrayList<>();
        for (DflBlackWhitePo typePo : whiteBlackList) {
            DflBlackWhiteVo vo = new DflBlackWhiteVo();
            vo.setCode(typePo.getCode());
            vo.setLimitType(typePo.getLimitType());
            vo.setName(typePo.getName());
            list.add(vo);
            String datas = itemList.stream().filter(t -> t.getBlackwhiteId().intValue() == typePo.getId())
                    .map(DflBlackWhiteItemPo::getData).collect(Collectors.joining(","));
            vo.setDatas(datas);
        }
        return list;
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE1H, key = "'opendfl:findBlackWhiteMaxUpdateTime:'+#type+':'+#limitType")
    public void findBlackWhiteMaxUpdateTime_evict(String type, Integer limitType) {
        logger.info("-----findBlackWhiteMaxUpdateTime_evict--type={} limitType={}", type, limitType);
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE1H, key = "'opendfl:findBlackWhiteMaxUpdateTime:'+#type+':'+#limitType")
    public Long findBlackWhiteMaxUpdateTime(String type, Integer limitType) {
        Date maxUpdateTime = this.mapper.findBlackWhiteMaxUpdateTime(type, limitType);
        if (maxUpdateTime != null) {
            return maxUpdateTime.getTime();
        }
        return 0L;
    }
}