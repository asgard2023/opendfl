package org.ccs.opendfl.mysql.dflbasedata.biz.impl;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.core.constants.CacheTimeType;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dflbasedata.biz.IDflTypeBiz;
import org.ccs.opendfl.mysql.dflbasedata.biz.IDflTypeItemBiz;
import org.ccs.opendfl.mysql.dflbasedata.mapper.DflTypeItemMapper;
import org.ccs.opendfl.mysql.dflbasedata.po.DflTypeItemPo;
import org.ccs.opendfl.mysql.dflbasedata.po.DflTypePo;
import org.ccs.opendfl.mysql.vo.TypeItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**

 * DflTypeItemBiz
 * 业务实现
 * @author chenjh
 * @date 2022-5-3 20:31:24
 */
@Service(value = "dflTypeItemBiz")
public class DflTypeItemBiz extends BaseService<DflTypeItemPo> implements IDflTypeItemBiz, ISelfInject {
    @Autowired
    private DflTypeItemMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflTypeItemBiz.class);

    @Override
    public Mapper<DflTypeItemPo> getMapper() {
        return mapper;
    }

    private IDflTypeItemBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflTypeItemBiz) o;
    }

    @Autowired
    private IDflTypeBiz dflTypeBiz;

    @Override
    public Example createConditions(DflTypeItemPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflTypeItemPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
        this.addEqualByKey(criteria, "typeId", otherParams);
        this.addEqualByKey(criteria, "status", otherParams);
        this.addEqualByKey(criteria, "code", otherParams);
        this.addEqualByKey(criteria, "name", otherParams);
    }

    @Override
    public MyPageInfo<DflTypeItemPo> findPageBy(DflTypeItemPo entity, MyPageInfo<DflTypeItemPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflTypeItemPo();
        }
        Example example = createConditions(entity, otherParams);
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflTypeItemPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    public List<DflTypeItemPo> getItemByTypeIds(List<Integer> typeIdList){
        if (CollectionUtils.isEmpty(typeIdList)) {
            return Collections.emptyList();
        }
        Example example = new Example(DflTypeItemPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("typeId", typeIdList);
        criteria.andEqualTo("status", 1);
        criteria.andEqualTo("ifDel", 0);
        return this.mapper.selectByExample(example);
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:dflType:getItemsByTypes:'+#lang+#codes")
    public Map<String, List<TypeItemVo>> getItemsByTypes(String lang, String codes){
        List<String> codeList = Arrays.asList(codes.split(","));
        List<DflTypePo> typePoList = dflTypeBiz.getTypeMapByCodes(codeList);
        List<Integer> typeIdList=typePoList.stream().map(DflTypePo::getId).collect(Collectors.toList());
        List<DflTypeItemPo> itemList=this.getItemByTypeIds(typeIdList);

        Map<String, List<TypeItemVo>> typeMap=new HashMap<>(typePoList.size());
        for(DflTypePo typePo: typePoList){
            List<DflTypeItemPo> typeItems=itemList.stream().filter(t->t.getTypeId().intValue()==typePo.getId()).collect(Collectors.toList());
            List<TypeItemVo> itemVoList=new ArrayList<>();
            TypeItemVo vo=null;
            for(DflTypeItemPo itemPo: typeItems){
                vo=new TypeItemVo();
                vo.setId(itemPo.getCode());
                if(StringUtils.isNumber(itemPo.getCode())){
                    vo.setId(Integer.parseInt(itemPo.getCode()));
                }
                vo.setName(itemPo.getName());
                vo.setColor(itemPo.getColor());
                itemVoList.add(vo);
            }
            typeMap.put(typePo.getCode(), itemVoList);
        }
        return typeMap;
    }

    @Override
    public List<TypeItemVo> getItemsByType(String lang, String code){
        Map<String, List<TypeItemVo>> typeMap= getItemsByTypes(lang, code);
        return typeMap.get(code);
    }

    @Override
    public Integer saveDflTypeItem(DflTypeItemPo entity) {
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
    public Integer updateDflTypeItem(DflTypeItemPo entity) {
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflTypeItem(Integer id, Integer operUser, String remark) {
        DflTypeItemPo po = new DflTypeItemPo();
        po.setId(id);
        // 0未删除,1已删除
        po.setIfDel(1);
        po.setModifyUser(operUser);
        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }
}