package org.ccs.opendfl.mysql.dflsystem.biz.impl;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.constants.CacheTimeType;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.constant.CommonStatus;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflSystemConfigBiz;
import org.ccs.opendfl.mysql.dflsystem.constant.ConfigValueType;
import org.ccs.opendfl.mysql.dflsystem.constant.SystemConfigCodes;
import org.ccs.opendfl.mysql.dflsystem.mapper.DflSystemConfigMapper;
import org.ccs.opendfl.mysql.dflsystem.po.DflSystemConfigPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Version V1.0
 * @Title: DflSystemConfigBiz
 * @Description: 系统参数配置(树形结构) 业务实现
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:27:48
 */
@Slf4j
@Service(value = "dflSystemConfigBiz")
@EnableAsync
public class DflSystemConfigBiz extends BaseService<DflSystemConfigPo> implements IDflSystemConfigBiz, ISelfInject {
    @Autowired
    private DflSystemConfigMapper mapper;

    static Logger logger = LoggerFactory.getLogger(DflSystemConfigBiz.class);

    @Override
    public Mapper<DflSystemConfigPo> getMapper() {
        return mapper;
    }

    private IDflSystemConfigBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflSystemConfigBiz) o;
    }

    @Override
    public Example createConditions(DflSystemConfigPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflSystemConfigPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
    public MyPageInfo<DflSystemConfigPo> findPageBy(DflSystemConfigPo entity, MyPageInfo<DflSystemConfigPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflSystemConfigPo();
        }
        Example example = createConditions(entity, otherParams);
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflSystemConfigPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    @Override
    public Integer saveDflSystemConfig(DflSystemConfigPo entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        if (entity.getValueType() == null) {
            entity.setValueType(0);
        }
        entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.mapper.insert(entity);
        return v;
    }

    @Override
    public Integer updateDflSystemConfig(DflSystemConfigPo entity) {
        DflSystemConfigPo exist = this.findById(entity.getId());
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        if (v > 0) {
            this._self.getConfigValue_evict(exist.getCode());
        }
        return v;
    }

    @Override
    public Integer deleteDflSystemConfig(Integer id, Integer operUser, String remark) {
        DflSystemConfigPo exist = this.findById(id);
        DflSystemConfigPo po = new DflSystemConfigPo();
        po.setId(id);
        po.setIfDel(1); // 0未删除,1已删除
        po.setModifyUser(operUser);
        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        if (v > 0) {
            this._self.getConfigValue_evict(exist.getCode());
        }
        return v;
    }

    @Override
    public List<Map<String, Object>> findSysconfigByParentIdsCount(Integer confType, List<Integer> pidList) {
        return mapper.findSysconfigByParentIdsCount(confType, pidList);
    }

    @Override
    public List<DflSystemConfigPo> findSysconfigByParentIds(Integer confType, List<Integer> parentIds) {
        logger.debug("----findSysconfigByParentIds--confType=" + confType + " parentIds=" + parentIds);
        if (parentIds.size() == 0) {
            return new ArrayList<>();
        }

        return this.mapper.findSysconfigByParentIds(confType, parentIds);
    }

    @Override
    public List<DflSystemConfigPo> getSysconfigByName(Integer confType, String name) {
        Example example = new Example(DflSystemConfigPo.class);
        example.selectProperties("name,code,id,parentId".split(","));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        criteria.andLike("name", name + "%");
        if (confType != null) {
            criteria.andEqualTo("confType", confType);
        }
        return this.mapper.selectByExample(example);
    }

    public DflSystemConfigPo getConfigByCode(String code) {
        ValidateUtils.notNull(code, "code is null");
        DflSystemConfigPo search = new DflSystemConfigPo();
        search.setCode(code);
        search.setIfDel(0);
        List<DflSystemConfigPo> list = this.findBy(search);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    @Async
    public DflSystemConfigPo saveSysConfigAsync(SystemConfigCodes systemConfigCodes, Integer parentId) {
        String configCode = systemConfigCodes.getCode();
        String title = systemConfigCodes.getName();
        String valueDefault = systemConfigCodes.getDefaultValue();
        try {
            DflSystemConfigPo exist = getConfigByCode(configCode);
            if (exist != null) {
                return exist;
            }
            DflSystemConfigPo sysConfig = new DflSystemConfigPo();
            sysConfig.setCode(configCode);
            sysConfig.setName(title);
            sysConfig.setValueType(systemConfigCodes.getValueType().getType());
            if (ConfigValueType.JSON == systemConfigCodes.getValueType()) {
                sysConfig.setValueJson(valueDefault);
            } else {
                sysConfig.setValue(valueDefault);
            }
            sysConfig.setValueDefault(valueDefault);
            sysConfig.setStatus(1);
            sysConfig.setParentId(parentId);
            sysConfig.setOrderCount(0);
            sysConfig.setRemark("code default");
            this.saveDflSystemConfig(sysConfig);
            return sysConfig;
        } catch (Exception e) {
            log.warn("------save--configCode={}, valueDefault={} title={} error={}", configCode, valueDefault, title, e.getMessage());
            return null;
        }
    }

    @Override
    @CacheEvict(value = CacheTimeType.CACHE30S, key = "'opendfl:sysConfig:getConfigValue:'+#configCode")
    public void getConfigValue_evict(String configCode) {
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:sysConfig:getConfigValue:'+#configCode")
    public <E> E getConfigValue(String configCode) {
        long time = System.currentTimeMillis();
        String value = null;
        DflSystemConfigPo po = this.getConfigByCode(configCode);
        if (po != null) {
            SystemConfigCodes systemConfigCodes = SystemConfigCodes.parse(configCode);
            if (systemConfigCodes != null) {
                //如果参数状态无效，返回默认值
                if (po.getStatus().intValue() == CommonStatus.INVALID.getStatus()) {
                    return ConfigValueType.getValue(systemConfigCodes.getValueType(), po.getValueDefault());
                }
                if (systemConfigCodes.getValueType() == ConfigValueType.INT) {
                    return ConfigValueType.getValue(systemConfigCodes.getValueType(), po.getValue());
                } else if (systemConfigCodes.getValueType() == ConfigValueType.JSON) {
                    return ConfigValueType.getValue(systemConfigCodes.getValueType(), po.getValueJson());
                } else {
                    return ConfigValueType.getValue(systemConfigCodes.getValueType(), po.getValue());
                }
            }
        } else {
            logger.warn("=====getConfigValue:configCode={} value={} time={}", configCode, value, System.currentTimeMillis() - time);
        }
        return null;
    }
}