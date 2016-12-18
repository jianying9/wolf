package com.wolf.framework.utils;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.ColumnHandler;
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
    
    public static <T extends Entity>Map<String, String> getMap( T t) {
        Map<String, String> entityMap = Collections.EMPTY_MAP;
        List<ColumnHandler> columnHandlerList = ApplicationContext.CONTEXT.getEntityInfo(t.getClass());
        if(columnHandlerList != null) {
            entityMap = new HashMap<>(columnHandlerList.size(), 1);
            String name;
            String value;
            for (ColumnHandler columnHandler : columnHandlerList) {
                name = columnHandler.getColumnName();
                value = columnHandler.getFieldStringValue(t);
                entityMap.put(name, value);
            }
        }
        return entityMap;
    }
    
}
