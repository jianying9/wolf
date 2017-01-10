package com.wolf.framework.utils;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class EntityUtils {

    public static <T extends Entity> Map<String, Object> getMap(T t) {
        Map<String, Object> entityMap = Collections.EMPTY_MAP;
        List<ColumnHandler> columnHandlerList = ApplicationContext.CONTEXT.getEntityInfo(t.getClass());
        if (columnHandlerList != null) {
            entityMap = new HashMap<>(columnHandlerList.size(), 1);
            String name;
            Object value;
            for (ColumnHandler columnHandler : columnHandlerList) {
                name = columnHandler.getColumnName();
                value = columnHandler.getFieldValue(t);
                entityMap.put(name, value);
            }
        }
        return entityMap;
    }

    public static <T extends Entity> Map<String, Object> getTempMap(Class<T> clazz) {
        Map<String, Object> entityMap = Collections.EMPTY_MAP;
        List<ColumnHandler> columnHandlerList = ApplicationContext.CONTEXT.getEntityInfo(clazz);
        if (columnHandlerList != null) {
            entityMap = new HashMap<>(columnHandlerList.size(), 1);
            String name;
            Object value;
            for (ColumnHandler columnHandler : columnHandlerList) {
                if (columnHandler.getColumnType().equals(ColumnType.KEY) == false) {
                    name = columnHandler.getColumnName();
                    value = columnHandler.getDefaultValue();
                    entityMap.put(name, value);
                }
            }
        }
        return entityMap;
    }

}
