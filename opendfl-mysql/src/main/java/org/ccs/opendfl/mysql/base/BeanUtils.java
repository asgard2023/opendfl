package org.ccs.opendfl.mysql.base;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class BeanUtils {

    /**
     * 首字母大写
     *
     * @param value
     * @return 首字母大写
     */
    public static String upperCaseFirst(String value) {
        if (CharSequenceUtil.isEmpty(value)) {
            return value;
        }
        char[] cs = value.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public static void main(String[] args) {
        class Abc {
            String giftId;

            public String getGiftId() {
                return giftId;
            }

            public void setGiftId(String giftId) {
                this.giftId = giftId;
            }

        }
        Abc vo = new Abc();
        setValue(vo, "giftId", "123");
    }

    /**
     * 通过反射设置实体的某属性值
     *
     * @param entity 对象
     * @param propertyName 属性名
     * @param value 属性值
     */
    public static void setValue(Object entity, String propertyName, Object value) {
        if (value == null) {
            return;
        }
        Method method = null;
        try {
            String methodName = "set" + upperCaseFirst(propertyName);
            method = entity.getClass().getDeclaredMethod(methodName, value.getClass());
            method.invoke(entity, value);
        } catch (Exception e) {
           log.error("----{} setValue--propertyName={}", e.getClass().getSimpleName(), propertyName, e);
        }

    }

    /**
     * 通过返回执行某个单参数方法
     *
     * @param entity 对象
     * @param methodName 方法名
     * @param params 参数值
     */
    public static Object executeMethod(Object entity, String methodName, Object params) {
        Method method = null;
        try {
            method = entity.getClass().getDeclaredMethod(methodName, params.getClass());
            return method.invoke(entity, params);
        }  catch (Exception e) {
            log.error("----{} executeMethod--propertyName={}", e.getClass().getSimpleName(), methodName, e);
        }
        return null;
    }

    /**
     * 获取实体的@Id 主键栏位名称
     *
     * @param entityClass 类
     * @return 主键值
     * @throws Exception 异常
     */
    public static String getPKPropertyName(Class<?> entityClass) {
        Set<EntityColumn> columnList = EntityHelper.getPKColumns(entityClass);
        if (columnList.size() == 1) {
            return columnList.iterator().next().getProperty();
        } else {
            throw new MapperException("实体类[" + entityClass.getCanonicalName() + "]中必须只有一个带有 @Id 注解的字段");
        }
    }

    /**
     * 通过反射获取实体某属性值
     *
     * @param entity 对象
     * @param propertyName 属性名
     * @return value
     */
    public static Object getValue(Object entity, String propertyName) {
        Method method = null;
        try {
            String methodName = "get" + upperCaseFirst(propertyName);
            method = entity.getClass().getDeclaredMethod(methodName);
            return method.invoke(entity);
        }  catch (Exception e) {
            log.error("----{} getValue--propertyName={}", e.getClass().getSimpleName(), propertyName, e);
        }
        return null;
    }

    public static Map<Object, Object> getMapProps(Collection<?> list, String keyProp, String valueProp) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Object, Object> result = new HashMap<>(list.size());
        for (Object obj : list) {
            result.put(getValue(obj, keyProp), getValue(obj, valueProp));
        }
        return result;
    }

    /**
     * 属性为空不返回
     *
     * @param list
     * @param propName
     * @return propName属性值的list
     * @throws Exception
     */
    public static List<Object> getPropsByName(Collection<?> list, String propName) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<Object> propDataList = new ArrayList<>(list.size());
        for (Object obj : list) {
            Object o = getValue(obj, propName);
            if (o != null) {
                propDataList.add(o);
            }
        }
        return propDataList;
    }

    /**
     * 属性为空不返回
     *
     * @param list
     * @param propName
     * @return propName属性值的list
     * @throws Exception
     */
    public static List<String> getStrPropsByName(Collection<?> list, String propName) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<String> propDataList = new ArrayList<>(list.size());
        String str = null;
        for (Object obj : list) {
            Object o = getValue(obj, propName);
            if (o != null) {
                if (o instanceof String) {
                    str = (String) o;
                } else {
                    str = "" + o;
                }
                propDataList.add(str);
            }
        }
        return propDataList;
    }

    public static List<String> toStringList(List<Object> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<String> list2 = new ArrayList<>(list.size());
        String str = null;
        for (Object o : list) {
            str = null;
            if (o != null) {
                if (o instanceof String) {
                    str = (String) o;
                } else {
                    str = "" + o;
                }
            }
            list2.add(str);
        }
        return list2;
    }

    /**
     * 从对象中获取指定的属性
     *
     * @param bean
     * @param property
     * @return obj
     * @throws IntrospectionException 异常
     * @throws IllegalAccessException 异常
     * @throws InvocationTargetException 异常
     */
    public static Object getObjectByProperty(Object bean, String property)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        if (CharSequenceUtil.isEmpty(property)) {
            return null;
        }
        property = property.trim();
        Class<?> type = bean.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class") && propertyName.equals(property)) {
                Method readMethod = descriptor.getReadMethod();
                return readMethod.invoke(bean, new Object[0]);
            }
        }
        return null;
    }
}
