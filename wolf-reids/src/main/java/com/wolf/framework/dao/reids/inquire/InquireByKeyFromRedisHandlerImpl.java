package com.wolf.framework.dao.reids.inquire;

import com.wolf.framework.dao.AbstractDaoHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.inquire.InquireByKeyHandler;
import com.wolf.framework.dao.reids.ColumnHandler;
import com.wolf.framework.dao.reids.RedisHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <T>
 */
public final class InquireByKeyFromRedisHandlerImpl<T extends Entity> extends AbstractDaoHandler<T> implements InquireByKeyHandler<T> {

    private final RedisHandler redisHandler;
    private final List<ColumnHandler> columnHandlerList;

    public InquireByKeyFromRedisHandlerImpl(RedisHandler redisHandler, Class<T> clazz, List<ColumnHandler> columnHandlerList) {
        super(clazz);
        this.redisHandler = redisHandler;
        this.columnHandlerList = columnHandlerList;
    }

    private void checkRedisData(Map<String, String> entityMap) {
        String columnName;
        for (ColumnHandler rColumnHandler : columnHandlerList) {
            columnName = rColumnHandler.getColumnName();
            if (entityMap.containsKey(columnName) == false) {
                entityMap.put(columnName, rColumnHandler.getDefaultValue());
            }
        }
    }

    @Override
    public T inquireByKey(String keyValue) {
        T t = null;
        Map<String, String> entityMap = this.redisHandler.inquireByKey(keyValue);
        if (entityMap != null) {
            this.checkRedisData(entityMap);
            t = this.newInstance(entityMap);
        }
        return t;
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        List<T> tList;
        List<Map<String, String>> entityMapList = this.redisHandler.inquireBykeys(keyValues);
        if (entityMapList.isEmpty() == false) {
            tList = new ArrayList<T>(entityMapList.size());
            T t;
            for (Map<String, String> entityMap : entityMapList) {
                this.checkRedisData(entityMap);
                t = this.newInstance(entityMap);
                tList.add(t);
            }
            tList = this.newInstance(entityMapList);
        } else {
            tList = new ArrayList<T>(0);
        }
        return tList;
    }
}
