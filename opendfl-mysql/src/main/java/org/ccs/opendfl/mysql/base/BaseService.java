/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.ccs.opendfl.mysql.base;


import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.util.*;

/**
 * Created by caco on 2017/07/21.
 */
public abstract class BaseService<T> implements IBaseService<T> {

    public abstract Mapper<T> getMapper();

    @Override
    public T selectByKey(Object key) {
        return getMapper().selectByPrimaryKey(key);
    }

    /**
     * 如果cid为null则进行插入并赋值cid，如果不为空则更新（包括空栏位更新）
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(T entity) {
        try {
            String pkName = BeanUtils.getPKPropertyName(entity.getClass());
            Object idValue = BeanUtils.getValue(entity, pkName);
            if (idValue == null || idValue.toString().length() == 0) {
                BeanUtils.setValue(entity, pkName, UUID.fastUUID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getMapper().insert(entity);
    }

    public void addEqualByKey(Example.Criteria criteria, String key, Map<String, Object> otherParams) {
        Object object = otherParams.get(key);
        if (object != null) {
            if (object instanceof String) {
                String value = (String) object;
                if (value.length() > 0) {
                    criteria.andEqualTo(key, value);
                }
            } else {
                criteria.andEqualTo(key, object);
            }
        }
    }

    /**
     * 支持JqGrid的动态查询filters参数处理
     *
     * @param criteria
     * @param otherParams
     */
    public void addFilters(Example.Criteria criteria, Map<String, Object> otherParams) {
        String filters = (String) otherParams.get("filters");
        if (!StringUtils.isEmpty(filters)) {
            JSONObject jsonFilter = (JSONObject) JSON.toJSON(filters);
            String groupOp = jsonFilter.getString("groupOp");
            JSONArray rules = jsonFilter.getJSONArray("rules");
            int rulesCount = rules.size();
            if ("AND".equals(groupOp)) {
                for (int i = 0; i < rulesCount; i++) {
                    JSONObject rule = rules.getJSONObject(i);
                    String field = rule.getString("field");
                    Object data = rule.get("data");
                    String op = rule.getString("op");
                    if ("eq".equals(op)) {//等于
                        criteria.andEqualTo(field, data);
                    } else if ("ne".equals(op)) {//不等于
                        criteria.andNotEqualTo(field, data);
                    } else if ("nn".equals(op)) {//存在
                        criteria.andIsNotNull(field);
                    } else if ("nu".equals(op)) {//不存在
                        criteria.andIsNull(field);
                    } else if ("in".equals(op)) {//属于
                        String datas = (String) data;
                        criteria.andIn(field, Arrays.asList(datas.split(",")));
                    } else if ("ni".equals(op)) {//不属于
                        String datas = (String) data;
                        criteria.andNotIn(field, Arrays.asList(datas.split(",")));
                    } else if ("cn".equals(op)) {//包含
                        String datas = (String) data;
                        criteria.andLike(field, datas);
                    } else if ("nc".equals(op)) {//不包含
                        String datas = (String) data;
                        criteria.andNotLike(field, datas);
                    } else if ("bw".equals(op)) {//开始于
                        criteria.andGreaterThanOrEqualTo(field, data);
                    }
//					else if("gt".equals(op)){
//						criteria.andGreaterThan(field, data);
//					}
//					else if("lt".equals(op)){
//						criteria.andLessThan(field, data);
//					}
                    else if ("ew".equals(op)) {//结束于
                        criteria.andLessThanOrEqualTo(field, data);
                    }
                }
            }
        }
    }


    /**
     * 删除
     *
     * @param key
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delete(Object key) {
        return getMapper().deleteByPrimaryKey(key);
    }

    /**
     * 更新对象所有属性，包括空值
     *
     * @param entity
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(T entity) {
        return getMapper().updateByPrimaryKey(entity);
    }

    /**
     * 只更新有值的属性
     *
     * @param entity
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateByPrimaryKeySelective(T entity) {
        return getMapper().updateByPrimaryKeySelective(entity);
    }


    /**
     * 软删除
     *
     * @param entity
     * @param propertyName
     * @param deleteStatus
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteVirtual(T entity, String propertyName, Object deleteStatus) throws Exception {
        BeanUtils.setValue(entity, propertyName, deleteStatus);
        return this.getMapper().updateByPrimaryKeySelective(entity);
    }

    /**
     * 根据id查询
     */
    @Override
    public T findById(Object id) {
        return this.getMapper().selectByPrimaryKey(id);
    }

    /**
     * 按id列表查询
     *
     * @param ids
     * @param entityClass
     * @return
     * @throws Exception
     */
    @Override
    public List<T> findByIds(List<Object> ids, Class<?> entityClass) {
        Example example = new Example(entityClass);
        Example.Criteria criteria = example.createCriteria();
        Set<EntityColumn> columnList = EntityHelper.getPKColumns(entityClass);
        if (columnList.size() != 1) {
            throw new RuntimeException("主键 不唯一");
        }
        criteria.andIn(BeanUtils.getPKPropertyName(entityClass), ids);//只取一个主键
        return this.getMapper().selectByExample(example);
    }

    @Override
    public List<T> findByPropotys(String propName, List<Object> propotys, Class<?> entityClass) throws Exception {
        return findByPropotys(propName, propotys, entityClass, null);
    }

    /**
     * 按id列表查询 like ： orderByClause: example.setOrderByClause("create_time desc");
     *
     * @param ids
     * @param entityClass
     * @return
     * @throws Exception
     */
    @Override
    public List<T> findByPropotys(String propName, List<Object> propotys, Class<?> entityClass, String orderByClause) throws Exception {
        if (CollectionUtils.isEmpty(propotys)) {
            return Collections.emptyList();
        }
        Example example = new Example(entityClass);
        if (orderByClause != null) {
            example.setOrderByClause(orderByClause);
        }
        Example.Criteria criteria = example.createCriteria();
        Set<EntityColumn> columnList = EntityHelper.getPKColumns(entityClass);
        if (columnList.size() != 1) {
            throw new Exception("主键 不唯一");
        }
        criteria.andIn(propName, propotys);
        return this.getMapper().selectByExample(example);
    }

    /**
     * 按照实体查找
     *
     * @param entity
     * @return
     * @throws Exception
     */
    @Override
    public List<T> findBy(T entity) {
        return this.getMapper().select(entity);
    }

    /**
     * 按属性进行唯一查询，如果有多个则会抛出异常
     *
     * @param propoty
     * @param value
     * @param entity
     * @return
     * @throws Exception
     */
    @Override
    public T findOne(String propoty, Object value, Class<T> entity) {
        List<T> list = findByPropoty(propoty, value, entity);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        if (list.size() > 1) {
            throw new RuntimeException("多条结果");
        }
        return list.get(0);
    }

    /**
     * 按属性查询list
     *
     * @param propoty
     * @param value
     * @param entity
     * @return
     * @throws Exception
     */
    @Override
    public List<T> findByPropoty(String propoty, Object value, Class<T> entity) {
        return findByPropoty(propoty, value, entity, null);
    }

    /**
     * 按属性查询list
     *
     * @param propoty
     * @param value
     * @param entity
     * @param orderByClause，like ： example.setOrderByClause("create_time desc");
     * @return
     * @throws Exception
     */
    @Override
    public List<T> findByPropoty(String propoty, Object value, Class<T> entity, String orderByClause) {
        Example example = new Example(entity);
        if (orderByClause != null) {
            example.setOrderByClause(orderByClause);
        }
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(propoty, value);
        return this.getMapper().selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "null"})
    @Override
    public MyPageInfo<T> findPageBy(T entity, MyPageInfo<T> pageInfo, Map<String, Object> otherParams) {
        Example example = createConditions(entity, otherParams);
//		if(StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
//			example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
//		}
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.isCountTotal());
        List<T> list = this.getMapper().selectByExample(example);
        boolean isCountTotal = pageInfo.isCountTotal();
        pageInfo = new MyPageInfo<>(list);
        pageInfo.setCountTotal(isCountTotal);
        if (!pageInfo.isCountTotal()) {
            pageInfo.setPages(100);
        }
        return pageInfo;
    }

    /**
     * @param list     要加载属性的list
     * @param byProp   通过哪个属性去关联
     * @param propId   关联对象的ID
     * @param propName 要加载的属性
     * @param clazz
     * @throws Exception
     */
    @Override
    public void loadProperty(List<?> list, String byProp, String propId, String propName, Class<T> clazz) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Object> ids = BeanUtils.getPropsByName(list, byProp);
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<T> tList = this.findByIds(ids, clazz);
        Map<Object, Object> map = new HashMap<>(tList.size());
        for (T t : tList) {
            map.put(BeanUtils.getValue(t, propId), BeanUtils.getValue(t, propName));
        }
        for (Object object : list) {
            BeanUtils.setValue(object, propName, map.get(BeanUtils.getValue(object, byProp)));
        }
    }

    @Override
    public void loadProperty(List<?> list, String byKey, String setName, String getKey, String getName, Class<T> clazz) throws Exception {
        if (CollectionUtils.isEmpty(list)) return;
        List<Object> props = BeanUtils.getPropsByName(list, byKey);
        List<T> findList = findByPropotys(getKey, props, clazz);
        Map<Object, Object> result = BeanUtils.getMapProps(findList, getKey, getName);
        for (Object o : list) {
            String key = (String) BeanUtils.getValue(o, byKey);
            if (key != null) {
                BeanUtils.setValue(o, setName, result.get(key));
            }
        }
    }

    public abstract Example createConditions(T entity, Map<String, Object> otherParams);

    @Override
    public Map<String, T> findMapByIds(List<Object> ids, Class<T> entity) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<T> pos = this.findByIds(ids, entity);
        if (CollectionUtils.isEmpty(pos)) {
            return Collections.emptyMap();
        }
        Map<String, T> map = new HashMap<String, T>();
        for (T po : pos) {
            Object tid = BeanUtils.getValue(po, "id");
            map.put(tid.toString(), po);
        }
        return map;
    }

}
