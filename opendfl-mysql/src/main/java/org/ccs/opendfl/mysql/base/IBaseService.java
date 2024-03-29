package org.ccs.opendfl.mysql.base;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface IBaseService<T> {
    public abstract Mapper<T> getMapper();

    public T selectByKey(Object key);

    /**
     * 如果cid为null则进行插入并赋值cid，如果不为空则更新（包括空栏位更新）
     */
    public int save(T entity);


    /**
     * 删除
     *
     * @param key
     * @return
     */
    public int delete(Object key);

    /**
     * 更新对象所有属性，包括空值
     *
     * @param entity
     * @return
     */
    public int update(T entity);

    /**
     * 只更新有值的属性
     *
     * @param entity
     * @return
     */
    public int updateByPrimaryKeySelective(T entity);


    /**
     * 通过id查找
     *
     * @param id
     * @return
     */
    public T findById(Object id);

    /**
     * 按id列表查询
     *
     * @param ids
     * @param entityClass
     * @return list
     */
    public List<T> findByIds(List<Object> ids, Class<?> entityClass);

    /**
     * 按照实体查找
     *
     * @param entity
     * @return
     */
    public List<T> findBy(T entity);

    public T findOne(String propoty, Object value, Class<T> entity);

    /**
     * 软删除
     *
     * @param entity
     * @param propertyName
     * @param deleteStatus
     * @return
     * @throws Exception
     */
    public int deleteVirtual(T entity, String propertyName, Object deleteStatus) throws Exception;

    /**
     * 分页查询
     *
     * @param entity      对象
     * @param pageInfo    翻页对象
     * @param otherParams
     * @return
     */
    MyPageInfo<T> findPageBy(T entity, MyPageInfo<T> pageInfo, Map<String, Object> otherParams);

    /**
     * 按某属性查询list
     *
     * @param propoty
     * @param value
     * @param entity
     * @return
     */
    List<T> findByPropoty(String propoty, Object value, Class<T> entity);

    /**
     * 按属性查询list  orderByClause ： example.setOrderByClause("create_time desc");
     *
     * @param propoty       propoty
     * @param value         value
     * @param entity        entity
     * @param orderByClause order条件
     * @return list
     */
    List<T> findByPropoty(String propoty, Object value, Class<T> entity, String orderByClause);

    void loadProperty(List<?> list, String byProp, String propId, String propName, Class<T> clazz) throws Exception;

    List<T> findByPropotys(String propName, List<Object> propotys, Class<?> entityClass, String orderByClause) throws Exception;

    void loadProperty(List<?> list, String byKey, String setName, String getKey, String getName, Class<T> clazz)
            throws Exception;

    List<T> findByPropotys(String propName, List<Object> propotys, Class<?> entityClass) throws Exception;

    /**
     * 通过ids转成Map
     *
     * @param ids    ids
     * @param entity entity
     * @return Map
     * @throws Exception entity
     */
    Map<String, T> findMapByIds(List<Object> ids, Class<T> entity) throws Exception;
}
