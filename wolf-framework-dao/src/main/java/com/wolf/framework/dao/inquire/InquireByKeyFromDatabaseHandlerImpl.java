package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.DatabaseHandler;
import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <T>
 */
public final class InquireByKeyFromDatabaseHandlerImpl<T extends Entity> implements InquireByKeyHandler<T> {

    private final DatabaseHandler databaseHandler;
    private final List<ColumnHandler> columnHandlerList;

    public InquireByKeyFromDatabaseHandlerImpl(DatabaseHandler databaseHandler, Class<T> clazz, List<ColumnHandler> columnHandlerList) {
        this.databaseHandler = databaseHandler;
        this.columnHandlerList = columnHandlerList;
    }
    
    private void checkRedisData(Map<String, String> entityMap) {
        String columnName;
        for (ColumnHandler rColumnHandler : columnHandlerList) {
            columnName = rColumnHandler.getColumnName();
            if (entityMap.containsKey(columnName) == false) {
//                entityMap.put(columnName, rColumnHandler.getDefaultValue());
            }
        }
    }

    @Override
    public T inquireByKey(String keyValue) {
        T t = null;
        Map<String, String> entityMap = this.databaseHandler.inquireByKey(keyValue);
        if (entityMap != null) {
            this.checkRedisData(entityMap);
        }
        return t;
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        List<T> tList;
        List<Map<String, String>> entityMapList = this.databaseHandler.inquireBykeys(keyValues);
        if (entityMapList.isEmpty() == false) {
            tList = new ArrayList<T>(entityMapList.size());
            T t;
            for (Map<String, String> entityMap : entityMapList) {
                this.checkRedisData(entityMap);
                //todo
//                tList.add(t);
            }
        } else {
            tList = new ArrayList<T>(0);
        }
        return tList;
    }
}
