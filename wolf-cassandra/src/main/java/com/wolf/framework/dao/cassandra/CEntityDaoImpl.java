package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <T>
 */
public class CEntityDaoImpl<T extends Entity> implements CEntityDao<T> {

    private final CassandraHandler cassandraHandler;
    private final List<ColumnHandler> keyHandlerList;
    private final List<ColumnHandler> columnHandlerList;
    private final Class<T> clazz;

    public CEntityDaoImpl(CassandraHandler cassandraHandler, List<ColumnHandler> keyHandlerList, List<ColumnHandler> columnHandlerList, Class<T> clazz) {
        this.cassandraHandler = cassandraHandler;
        this.keyHandlerList = keyHandlerList;
        this.columnHandlerList = columnHandlerList;
        this.clazz = clazz;
    }

    @Override
    public boolean exist(Object keyValue) {
        return this.cassandraHandler.exist(keyValue);
    }

    private T readMap(Map<String, Object> entityMap) {
        T t = null;
        if (entityMap != null) {
            Field field;
            Object value;
            try {
                t = this.clazz.newInstance();
                for (ColumnHandler key : this.keyHandlerList) {
                    value = entityMap.get(key.getColumnName());
                    field = key.getField();
                    field.setAccessible(true);
                    field.set(t, value);
                    field.setAccessible(false);
                }
                for (ColumnHandler key : this.columnHandlerList) {
                    value = entityMap.get(key.getColumnName());
                    field = key.getField();
                    field.setAccessible(true);
                    field.set(t, value);
                    field.setAccessible(false);
                }
            } catch (InstantiationException ex) {
            } catch (IllegalAccessException ex) {
            }
        }
        return t;
    }

    @Override
    public T inquireByKey(Object keyValue) {
        Map<String, Object> entityMap = this.cassandraHandler.inquireByKey(keyValue);
        return this.readMap(entityMap);
    }

    @Override
    public Object[] insert(Map<String, Object> entityMap) {
        return this.cassandraHandler.insert(entityMap);
    }

    @Override
    public T insertAndInquire(Map<String, Object> entityMap) {
        Object[] keyValue = this.insert(entityMap);
        return this.inquireByKey(keyValue);
    }

    @Override
    public void batchInsert(List<Map<String, Object>> entityMapList) {
        this.cassandraHandler.batchInsert(entityMapList);
    }

    @Override
    public Object[] update(Map<String, Object> entityMap) {
        return this.cassandraHandler.update(entityMap);
    }

    @Override
    public void batchUpdate(List<Map<String, Object>> entityMapList) {
        this.cassandraHandler.batchUpdate(entityMapList);
    }

    @Override
    public T updateAndInquire(Map<String, Object> entityMap) {
        Object[] keyValue = this.cassandraHandler.update(entityMap);
        return this.inquireByKey(keyValue);
    }

    @Override
    public void delete(Object keyValue) {
        this.cassandraHandler.delete(keyValue);
    }

    @Override
    public void batchDelete(List<Object[]> keyValues) {
        this.cassandraHandler.batchDelete(keyValues);
    }

    @Override
    public long count() {
        return this.cassandraHandler.count();
    }
}
