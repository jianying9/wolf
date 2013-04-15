package com.wolf.framework.dao.cache;

import com.wolf.framework.dao.condition.Condition;
import com.wolf.framework.dao.condition.InquireContext;
import java.util.List;

/**
 * 查询集合查询结果缓存管理对象
 *
 * @author neslon, aladdin
 */
public interface InquireCache {

    public void putInquireKeysCache(final InquireContext inquireContext, final List<String> keyList);

    public List<String> getInquireKeysCache(final InquireContext inquireContext);
    
    public void putCountCache(final List<Condition> conditionList, Integer count);

    public Integer getCountCache(final List<Condition> conditionList);

    public void removeCache();
}
