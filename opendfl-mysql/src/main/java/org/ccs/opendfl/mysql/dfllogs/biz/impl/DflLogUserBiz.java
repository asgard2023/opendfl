package org.ccs.opendfl.mysql.dfllogs.biz.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.ccs.opendfl.core.constants.ReqSysType;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflLogUserBiz;
import org.ccs.opendfl.mysql.dfllogs.mapper.DflLogUserMapper;
import org.ccs.opendfl.mysql.dfllogs.po.DflLogUserPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 业务实现
 * @Title: DflLogUserBiz
 * @Author: Created by chenjh
 * @Date: 2022-5-10 22:16:27
 */
@Service(value = "dflLogUserBiz")
public class DflLogUserBiz extends BaseService<DflLogUserPo> implements IDflLogUserBiz, ISelfInject {
    @Autowired
    private DflLogUserMapper mapper;


    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplateString;

    static Logger logger = LoggerFactory.getLogger(DflLogUserBiz.class);

    @Override
    public Mapper<DflLogUserPo> getMapper() {
        return mapper;
    }

    private IDflLogUserBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflLogUserBiz) o;
    }

    @Override
    public Example createConditions(DflLogUserPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflLogUserPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
    public MyPageInfo<DflLogUserPo> findPageBy(DflLogUserPo entity, MyPageInfo<DflLogUserPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflLogUserPo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflLogUserPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }


    public static final Cache<String, Long> userUidMap = CacheBuilder.newBuilder().expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(10000).build();

    public void loadUserIdMap(String userId) {
        Example example = new Example(DflLogUserPo.class);
        example.selectProperties("id,userId,ifDel".split(","));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        if (StringUtil.isNotEmpty(userId)) {
            criteria.andEqualTo("userId", userId);
        }
        List<DflLogUserPo> scansPos = this.mapper.selectByExample(example);
        for (DflLogUserPo scansPo : scansPos) {
            userUidMap.put(scansPo.getUserId(), scansPo.getId());
            putUidByCache(scansPo.getUserId(), scansPo.getId());
        }
    }

    @Override
    public Long getUid(String userId, String sysType, String ip) {
        if (userId == null) {
            return null;
        }
        Long id = userUidMap.getIfPresent(userId);
        if (id == null) {
            if (id == null) {
                id = getUidByCache(userId);
            }
            synchronized (userId.intern()) {
                if (id == null) {
                    loadUserIdMap(userId);
                    id = userUidMap.getIfPresent(userId);
                }
                if (id == null) {
                    DflLogUserPo entity = new DflLogUserPo();
                    entity.setUserId(userId);
//                    entity.setStatus(1);
                    entity.setIfDel(0);
                    entity.setSysType(ReqSysType.getSysType(sysType));
                    entity.setRegisterIp(ip);
                    entity.setCreateTime(new Date());
                    this.saveDflLogUser(entity);
                    id = entity.getId();
                    if (id != null) {
                        userUidMap.put(userId, id);
                        this.putUidByCache(userId, id);
                    }
                }
            }
        }

        return id;
    }

    private String getUidRedisKey() {
        Date date = new Date();
        String redisKey = "opoendfl:dflUserUid:" + DateUtil.dayOfWeek(date);
        return redisKey;
    }

    private Long getUidByCache(String userId) {
        Long id = null;
        String redisKey = getUidRedisKey();
        String idStr = (String) redisTemplateString.opsForHash().get(redisKey, userId);
        if (idStr != null) {
            id = Long.parseLong(idStr);
        }
        return id;
    }

    private void putUidByCache(String userId, Long id) {
        if (id == null) {
            return;
        }
        String redisKey = getUidRedisKey();
        redisTemplateString.opsForHash().put(redisKey, userId, "" + id);
    }

    @Override
    public Map<Long, DflLogUserPo> getUserPos(List<Long> userUidList) {
        if (CollectionUtils.isEmpty(userUidList)) {
            return Collections.emptyMap();
        }
        Example example = new Example(DflLogUserPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        criteria.andIn("id", userUidList);
        List<DflLogUserPo> userList = this.mapper.selectByExample(example);
        Map<Long, DflLogUserPo> userMap = new HashMap<>();
        for (DflLogUserPo scansPo : userList) {
            userMap.put(scansPo.getId(), scansPo);
        }
        return userMap;
    }


    @Override
    public Integer saveDflLogUser(DflLogUserPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
//		entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.mapper.insert(entity);
        return v;
    }

    @Override
    public Integer updateDflLogUser(DflLogUserPo entity) {
//		entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflLogUser(Long id, Integer operUser, String remark) {
        DflLogUserPo po = new DflLogUserPo();
        po.setId(id);
        po.setIfDel(1); // 0未删除,1已删除
//		po.setModifyUser(operUser);
        po.setRemark(remark);
//		po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }
}