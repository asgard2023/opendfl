package org.ccs.opendfl.mysql.base;

import com.github.pagehelper.PageInfo;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class MyPageInfo<T> extends PageInfo<T> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean countTotal = true;

    /**
     * 排序方向
     */
    private String order;
    /**
     * 排序字段
     */
    private String orderBy;
    private Map<String, Object> dicts;
    private Object summary;
    private Collection footer = new ArrayList();

    public MyPageInfo() {
        super();
    }

    /**
     * 包装Page对象
     *
     * @param list
     */
    public MyPageInfo(List<T> list) {
        super(list, 8);
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }


    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    //	@Override
    public Map<String, Object> getDicts() {
        return dicts;
    }

    public void setDicts(Map<String, Object> dicts) {
        this.dicts = dicts;
    }

    public void addDict(String key, Object map) {
        if (dicts == null) {
            dicts = new TreeMap<>();
        }
        dicts.put(key, map);
    }

    //	@Override
    public Object getSummary() {
        return summary;
    }

    public void setSummary(Object summary) {
        this.summary = summary;
    }

    public Collection getFooter() {
        if (CollectionUtils.isEmpty(footer) && summary != null && summary instanceof Collection) {
            return (Collection) summary;
        }
        return footer;
    }

    public void setFooter(Collection footer) {
        this.footer = footer;
    }

    public boolean isCountTotal() {
        return countTotal;
    }

    public void setCountTotal(boolean countTotal) {
        this.countTotal = countTotal;
    }
}
