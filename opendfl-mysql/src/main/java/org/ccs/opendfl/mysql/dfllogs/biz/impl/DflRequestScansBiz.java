package org.ccs.opendfl.mysql.dfllogs.biz.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.ccs.opendfl.core.utils.AnnotationControllerUtils;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestVo;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dfllogs.biz.IDflRequestScansBiz;
import org.ccs.opendfl.mysql.dfllogs.mapper.DflRequestScansMapper;
import org.ccs.opendfl.mysql.dfllogs.po.DflRequestScansPo;
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
 * 所有接口方法（通过扫码所有controller接口） 业务实现
 * DflRequestScansBiz
 *
 * @author chenjh
 * @date 2022-5-10 22:12:23
 */
@Service(value = "dflRequestScansBiz")
public class DflRequestScansBiz extends BaseService<DflRequestScansPo> implements IDflRequestScansBiz, ISelfInject {
    @Autowired
    private DflRequestScansMapper mapper;

    @Resource(name = "redisTemplateString")
    private RedisTemplate<String, String> redisTemplateString;

    static Logger logger = LoggerFactory.getLogger(DflRequestScansBiz.class);

    @Override
    public Mapper<DflRequestScansPo> getMapper() {
        return mapper;
    }

    private IDflRequestScansBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflRequestScansBiz) o;
    }

    @Override
    public Example createConditions(DflRequestScansPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflRequestScansPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
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
        this.addEqualByKey(criteria, "method", otherParams);
        this.addEqualByKey(criteria, "beanName", otherParams);
        this.addEqualByKey(criteria, "status", otherParams);
    }

    @Override
    public MyPageInfo<DflRequestScansPo> findPageBy(DflRequestScansPo entity, MyPageInfo<DflRequestScansPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflRequestScansPo();
        }
        Example example = createConditions(entity, otherParams);

        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflRequestScansPo> list = this.getMapper().selectByExample(example);
        return new MyPageInfo<>(list);
    }

    public static final Cache<String, Integer> uriIdMap = CacheBuilder.newBuilder().expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(200).build();

    public void loadUriIdMap(String uri) {
        Example example = new Example(DflRequestScansPo.class);
        example.selectProperties("id,uri,ifDel".split(","));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        if (StringUtil.isNotEmpty(uri)) {
            criteria.andEqualTo("uri", uri);
        }
        List<DflRequestScansPo> scansPos = this.mapper.selectByExample(example);
        for (DflRequestScansPo scansPo : scansPos) {
            uriIdMap.put(scansPo.getUri(), scansPo.getId());
            putUriIdByCache(scansPo.getUri(), scansPo.getId());
        }
    }

    @Override
    public Integer getUriId(String uri) {
        if (uri == null) {
            return null;
        }
        Integer id = uriIdMap.getIfPresent(uri);
        if (id == null) {
            if (id == null) {
                id = getUriIdByCache(uri);
            }
            synchronized (uri.intern()) {
                if (id == null) {
                    loadUriIdMap(uri);
                    id = uriIdMap.getIfPresent(uri);
                }
                if (id == null) {
                    DflRequestScansPo entity = new DflRequestScansPo();
                    entity.setUri(uri);
                    entity.setStatus(1);
                    entity.setIfDel(0);
                    entity.setCreateTime(new Date());
                    this.saveDflRequestScans(entity);
                    id = entity.getId();
                    if (id != null) {
                        uriIdMap.put(uri, id);
                        this.putUriIdByCache(uri, id);
                    }
                }
            }
        }

        return id;
    }

    private String getUriIdRedisKey() {
        Date date = new Date();
        return "opendfl:requestScansUriId:" + DateUtil.dayOfWeek(date);
    }

    private Integer getUriIdByCache(String uri) {
        Integer id = null;
        String redisKey = getUriIdRedisKey();
        String idStr = (String) redisTemplateString.opsForHash().get(redisKey, uri);
        if (idStr != null) {
            id = Integer.parseInt(idStr);
        }
        return id;
    }

    private void putUriIdByCache(String uri, Integer id) {
        if (id == null) {
            return;
        }
        String redisKey = getUriIdRedisKey();
        redisTemplateString.opsForHash().put(redisKey, uri, "" + id);
    }

    @Override
    public Map<Integer, DflRequestScansPo> getUriPos(List<Integer> uriIdList) {
        if (CollectionUtils.isEmpty(uriIdList)) {
            return Collections.emptyMap();
        }
        Example example = new Example(DflRequestScansPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ifDel", 0);
        criteria.andIn("id", uriIdList);
        List<DflRequestScansPo> uriList = this.mapper.selectByExample(example);
        Map<Integer, DflRequestScansPo> uriMap = new HashMap<>(uriList.size());
        for (DflRequestScansPo scansPo : uriList) {
            uriMap.put(scansPo.getId(), scansPo);
        }
        return uriMap;
    }

    @Override
    public Integer saveDflRequestScans(DflRequestScansPo entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.mapper.insertSelective(entity);
        return v;
    }

    @Override
    public Integer updateDflRequestScans(DflRequestScansPo entity) {
        entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflRequestScans(Integer id, Integer operUser, String remark) {
        DflRequestScansPo po = new DflRequestScansPo();
        po.setId(id);
        // 0未删除,1已删除
        po.setIfDel(1);
        po.setModifyUser(operUser);
        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }

    public List<DflRequestScansPo> findAll(Integer ifDel) {
        DflRequestScansPo search = new DflRequestScansPo();
        search.setIfDel(ifDel);
        return this.findBy(search);
    }

    @Override
    public Integer updateScanController(String pkg, Integer operUser) {
        pkg = (String) CommUtils.nvl(pkg, "org.ccs.opendfl");
        logger.info("-----updateScanController--pkg={}", pkg);
        List<DflRequestScansPo> list = this.findAll(0);
        List<RequestVo> scans = AnnotationControllerUtils.getControllerRequests(pkg);
        logger.info("-----updateScanController--existList={} scans={}", list.size(), scans.size());
        DflRequestScansPo scansPo = null;
        int count = 0;
        boolean isChange = false;
        for (RequestVo requestVo : scans) {
            Optional<DflRequestScansPo> scansOp = list.stream().filter(t -> StringUtils.equals(requestVo.getRequestUri(), t.getUri())).findFirst();
            if (scansOp.isPresent()) {
                scansPo = scansOp.get();
                isChange = this.changeData(scansPo, requestVo, operUser);
            } else {
                scansPo = this.addNewRequest(requestVo, operUser);
                isChange = true;
                list.add(scansPo);
            }
            if (isChange) {
                count++;
            }
        }
        return count;
    }

    private DflRequestScansPo addNewRequest(RequestVo requestVo, Integer operUser) {
        DflRequestScansPo scansPo = new DflRequestScansPo();
        scansPo.setUri(requestVo.getRequestUri());
        scansPo.setMethod(requestVo.getMethod());
        scansPo.setBeanName(requestVo.getBeanName());
        scansPo.setAnnotations(requestVo.getAnnotations());
        scansPo.setMethodName(requestVo.getMethodName());
        scansPo.setCreateUser(operUser);
        scansPo.setModifyUser(operUser);
        scansPo.setIfDel(0);
        scansPo.setStatus(1);
        this.saveDflRequestScans(scansPo);
        return scansPo;
    }

    private boolean changeData(DflRequestScansPo scansPo, RequestVo requestVo, Integer operUser) {
        if (StringUtils.isNotBlank(scansPo.getMethod()) && StringUtils.isNotBlank(scansPo.getBeanName())) {
            return false;
        }
        scansPo.setMethod(requestVo.getMethod());
        scansPo.setBeanName(requestVo.getBeanName());
        scansPo.setAnnotations(requestVo.getAnnotations());
        scansPo.setMethodName(requestVo.getMethodName());
        scansPo.setModifyUser(operUser);
        scansPo.setPkg(requestVo.getPkg());
        this.updateDflRequestScans(scansPo);
        return true;
    }
}