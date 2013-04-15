package com.wolf.framework.dao.cache;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.condition.Condition;
import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.dao.condition.OperateTypeEnum;
import com.wolf.framework.dao.condition.Order;
import com.wolf.framework.dao.condition.OrderTypeEnum;
import com.wolf.framework.logger.LogFactory;
import java.util.Collections;
import java.util.List;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;

/**
 *
 * @author neslon, aladdin
 */
public final class InquireCacheImpl implements InquireCache {

    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.DAO);
    //缓存数据对象
    private final Cache cache;
    //
    private final ConditionSort conditionSort = new ConditionSort();

    public InquireCacheImpl(Cache cache) {
        this.cache = cache;
    }
    
    private String getCacheKey(final InquireContext inquireContext) {
        String columnName;
        String columnValue;
        OperateTypeEnum operateTypeEnum;
        final List<Condition> conditionList = inquireContext.getConditionList();
        StringBuilder keyBuilder = new StringBuilder(conditionList.size() * 24 + 24);
        //page
        int pageIndex = inquireContext.getPageIndex();
        if(pageIndex > 0) {
            keyBuilder.append("pageIndex_").append(inquireContext.getPageIndex()).append("|pageSize_").append(inquireContext.getPageSize()).append('|');
        }
        //condition
        if (conditionList.size() == 1) {
            Condition con = conditionList.get(0);
            columnName = con.getColumnName();
            columnValue = con.getColumnValue();
            operateTypeEnum = con.getOperateTypeEnum();
            keyBuilder.append(columnName).append('_').append(operateTypeEnum.name()).append('_').append(columnValue).append('|');
        } else {
            Collections.sort(conditionList, this.conditionSort);
            for (Condition condition : conditionList) {
                columnName = condition.getColumnName();
                columnValue = condition.getColumnValue();
                operateTypeEnum = condition.getOperateTypeEnum();
                keyBuilder.append(columnName).append('_').append(operateTypeEnum.name()).append('_').append(columnValue).append('|');
            }
        }
        //order
        List<Order> orderList = inquireContext.getOrderList();
            if (orderList.isEmpty() == false) {
                keyBuilder.append("order:");
                for (Order order : orderList) {
                    keyBuilder.append(order.getColumnName());
                    if (order.getOrderType() == OrderTypeEnum.DESC) {
                        keyBuilder.append(" desc");
                    }
                    keyBuilder.append(',');
                }
                keyBuilder.setLength(keyBuilder.length() - 1);
            }
        String conditionStr = keyBuilder.toString();
        this.logger.debug("cache key:{}", conditionStr);
        return conditionStr;
    }

    @Override
    public void putInquireKeysCache(final InquireContext inquireContext, final List<String> keyList) {
        String key = this.getCacheKey(inquireContext);
        Element element = new Element(key, keyList);
        this.cache.put(element);
    }

    /**
     * 缓存未命中返回null
     *
     * @param tableName
     * @param conditionList
     * @return
     */
    @Override
    public List<String> getInquireKeysCache(final InquireContext inquireContext) {
        List<String> keyList = null;
        String key = this.getCacheKey(inquireContext);
        this.logger.debug("key cache: finding Key:{}", key);
        Element element = this.cache.getQuiet(key);
        if (element != null) {
            keyList = (List<String>) element.getObjectValue();
            this.logger.debug("key cache: find:{}", key);
        } else {
            this.logger.debug("key cache: not find :{}", key);
        }
        return keyList;
    }

    /**
     * 移除缓存
     *
     * @param companyId 公司ID
     * @param tableName 表名
     */
    @Override
    public void removeCache() {
        this.cache.removeAll();
    }
    
    
    private String getCacheKey(final List<Condition> conditionList) {
        String columnName;
        String columnValue;
        OperateTypeEnum operateTypeEnum;
        StringBuilder keyBuilder = new StringBuilder(conditionList.size() * 24);
        if (conditionList.size() == 1) {
            Condition con = conditionList.get(0);
            columnName = con.getColumnName();
            columnValue = con.getColumnValue();
            operateTypeEnum = con.getOperateTypeEnum();
            keyBuilder.append(columnName).append('_').append(operateTypeEnum.name()).append('_').append(columnValue);
        } else {
            Collections.sort(conditionList, this.conditionSort);
            for (Condition condition : conditionList) {
                columnName = condition.getColumnName();
                columnValue = condition.getColumnValue();
                operateTypeEnum = condition.getOperateTypeEnum();
                keyBuilder.append(columnName).append('_').append(operateTypeEnum.name()).append('_').append(columnValue).append('|');
            }
            keyBuilder.setLength(keyBuilder.length() - 1);
        }
        String conditionStr = keyBuilder.toString();
        this.logger.debug("cache key:{}", conditionStr);
        return conditionStr;
    }

    @Override
    public void putCountCache(List<Condition> conditionList, Integer count) {
        String key = this.getCacheKey(conditionList);
        Element element = new Element(key, count);
        this.cache.put(element);
    }

    @Override
    public Integer getCountCache(List<Condition> conditionList) {
        Integer count = null;
        String key = this.getCacheKey(conditionList);
        this.logger.debug("key cache: finding Key:{}", key);
        Element element = this.cache.getQuiet(key);
        if (element != null) {
            count = (Integer) element.getObjectValue();
            this.logger.debug("key cache: find:{}", key);
        } else {
            this.logger.debug("key cache: not find :{}", key);
        }
        return count;
    }
}
