package org.ccs.opendfl.mysql.dfllogs.biz.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflLogUserBiz;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflOutLimitLogBiz;
import org.ccs.opendfl.mysql.dfllogs.mapper.DflOutLimitLogMapper;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLimitLogPo;
import org.ccs.opendfl.mysql.dfllogs.vo.DflOutLimitLogCountVo;
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
 * @Version V1.0
 * @Title: DflOutLimitLogBiz
 * @Description: 频率限制超限日志 业务实现
 * @Author: Created by chenjh
 * @Date: 2022-5-6 23:21:44
 */
@Service(value = "dflOutLimitLogBiz")
public class DflOutLimitLogBiz extends BaseService<DflOutLimitLogPo> implements IDflOutLimitLogBiz, ISelfInject {
    @Autowired
    private DflOutLimitLogMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflOutLimitLogBiz.class);

    @Override
    public Mapper<DflOutLimitLogPo> getMapper() {
        return mapper;
    }

    private IDflOutLimitLogBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflOutLimitLogBiz) o;
    }

    @Autowired
    private IDflLogUserBiz dflLogUserBiz;

    @Override
    public Example createConditions(DflOutLimitLogPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflOutLimitLogPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
        this.addEqualByKey(criteria, "userId", otherParams);
        this.addEqualByKey(criteria, "ip", otherParams);
        this.addEqualByKey(criteria, "limitType", otherParams);
        this.addEqualByKey(criteria, "outLimitType", otherParams);
        this.addEqualByKey(criteria, "subType", otherParams);
        this.addEqualByKey(criteria, "ifResource", otherParams);
    }

    @Override
    public MyPageInfo<DflOutLimitLogPo> findPageBy(DflOutLimitLogPo entity, MyPageInfo<DflOutLimitLogPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflOutLimitLogPo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflOutLimitLogPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveDflOutLimitLog(DflOutLimitLogPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        int v = this.mapper.insert(entity);
        return v;
    }

    @Override
    public Integer updateDflOutLimitLog(DflOutLimitLogPo entity) {
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflOutLimitLog(Long id, Integer operUser, String remark) {
        DflOutLimitLogPo po = new DflOutLimitLogPo();
        po.setId(id);
        po.setRemark(remark);
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }

    public List<DflOutLimitLogCountVo> countFreqLogs(DflOutLimitLogPo entity, Map<String, Object> paramsMap, MyPageInfo pageInfo) {
        String startTime = (String) paramsMap.get("startTime");
        String endTime = (String) paramsMap.get("endTime");
        String orderBy = pageInfo.getOrderBy();
        if (StringUtils.isBlank(entity.getUri())) {
            entity.setUri(null);
        }

        String findType = (String) paramsMap.get("findType");
        String userId = (String) paramsMap.get("userId");
        if (StringUtils.isNotBlank(userId)) {
            Long userUid = dflLogUserBiz.getUid(userId, null, null);
            if (userUid == null) {
                userUid = -1L;
            }
            entity.setUid(userUid);
        }

        if(endTime==null){
            Date curDate = new Date();
            endTime= DateUtil.formatDateTime(curDate);
        }
        logger.info("----countFreqLogs--startTime={} endTime={} userId={} orderBy={}", startTime, endTime, userId, orderBy);
        String order = orderBy + " " + pageInfo.getOrder();
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize(), false).setOrderBy(order);
        return this.mapper.countFreqLogs(entity.getUriId(), findType, entity.getOutLimitType(), entity.getLimitType(), entity.getLimitCount(), entity.getTimeSecond(), entity.getUid(), startTime, endTime);
    }
}