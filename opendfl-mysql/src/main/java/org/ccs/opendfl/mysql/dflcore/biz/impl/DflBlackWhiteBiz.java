package org.ccs.opendfl.mysql.dflcore.biz.impl;

import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.constant.CommonIf;
import org.ccs.opendfl.mysql.constant.CommonStatus;
import org.ccs.opendfl.mysql.dflcore.biz.IDflBlackWhiteBiz;
import org.ccs.opendfl.mysql.dflcore.biz.IDflBlackWhiteItemBiz;
import org.ccs.opendfl.mysql.dflcore.mapper.DflBlackWhiteMapper;
import org.ccs.opendfl.mysql.dflcore.po.DflBlackWhitePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 黑白名单 业务实现
 * DflBlackWhiteBiz
 *
 * @author chenjh
 * @date 2022-5-18 21:44:47
 */
@Service(value = "dflBlackWhiteBiz")
public class DflBlackWhiteBiz extends BaseService<DflBlackWhitePo> implements IDflBlackWhiteBiz, ISelfInject {
    @Autowired
    private DflBlackWhiteMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflBlackWhiteBiz.class);

    @Override
    public Mapper<DflBlackWhitePo> getMapper() {
        return mapper;
    }

    private IDflBlackWhiteBiz _self;

    @Autowired
    @Lazy(true)
    private IDflBlackWhiteItemBiz dflBlackWhiteItemBiz;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflBlackWhiteBiz) o;
    }

    @Override
    public Example createConditions(DflBlackWhitePo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflBlackWhitePo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
    public MyPageInfo<DflBlackWhitePo> findPageBy(DflBlackWhitePo entity, MyPageInfo<DflBlackWhitePo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflBlackWhitePo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflBlackWhitePo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveDflBlackWhite(DflBlackWhitePo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(CommonIf.NO.getType());
        }
        int v = this.mapper.insert(entity);
        if (v > 0) {
            this.dflBlackWhiteItemBiz.findBlackWhiteList_evict(entity.getType(), entity.getLimitType());
            this.dflBlackWhiteItemBiz.findBlackWhiteMaxUpdateTime_evict(entity.getType(), entity.getLimitType());
            this.dflBlackWhiteItemBiz.findBlackWhiteMaxUpdateTime_evict(entity.getType(), null);
        }
        return v;
    }

    @Override
    public Integer updateDflBlackWhite(DflBlackWhitePo entity) {
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        if (v > 0) {
            this.dflBlackWhiteItemBiz.findBlackWhiteList_evict(entity.getType(), entity.getLimitType());
            this.dflBlackWhiteItemBiz.findBlackWhiteMaxUpdateTime_evict(entity.getType(), entity.getLimitType());
            this.dflBlackWhiteItemBiz.findBlackWhiteMaxUpdateTime_evict(entity.getType(), null);
        }
        return v;
    }

    @Override
    public Integer deleteDflBlackWhite(Integer id, Integer operUser, String remark) {
        DflBlackWhitePo exist = this.findById(id);
        DflBlackWhitePo po = new DflBlackWhitePo();
        po.setId(id);
        po.setIfDel(CommonIf.YES.getType()); // 0未删除,1已删除
        po.setModifyUser(operUser);
        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        if (v > 0) {
            this.dflBlackWhiteItemBiz.findBlackWhiteList_evict(exist.getType(), exist.getLimitType());
            this.dflBlackWhiteItemBiz.findBlackWhiteMaxUpdateTime_evict(exist.getType(), exist.getLimitType());
            this.dflBlackWhiteItemBiz.findBlackWhiteMaxUpdateTime_evict(exist.getType(), null);
        }
        return v;
    }

    @Override
    public List<DflBlackWhitePo> findBlackWhiteByType(String type, Integer limitType) {
        DflBlackWhitePo search = new DflBlackWhitePo();
        search.setType(type);
        search.setLimitType(limitType);
        search.setIfDel(CommonIf.NO.getType());
        search.setStatus(CommonStatus.VALID.getStatus());
        return this.findBy(search);
    }
}