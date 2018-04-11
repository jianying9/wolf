package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.ResultSet;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class CEntityCacheDaoImpl<T extends Entity> implements CEntityDao<T> {

    //缓存数据对象
    private final Cache cache;

    private final CEntityDaoImpl<T> cEntityDaoImpl;

    public CEntityCacheDaoImpl(Cache cache, CEntityDaoImpl<T> cEntityDaoImpl) {
        this.cache = cache;
        this.cEntityDaoImpl = cEntityDaoImpl;
    }

    private String getKeyValue(Object keyValue, ColumnHandler columnHandler) {
        String result = "";
        switch (columnHandler.getColumnDataType()) {
            case LONG:
                Long l = (Long) keyValue;
                result = l.toString();
                break;
            case INT:
                Integer i = (Integer) keyValue;
                result = i.toString();
                break;
            case DOUBLE:
                Double d = (Double) keyValue;
                result = d.toString();
                break;
            case BOOLEAN:
                Boolean b = (Boolean) keyValue;
                result = b.toString();
                break;
            case STRING:
                result = (String) keyValue;
                break;
        }
        return result;
    }

    private String getKeyValue(Map<String, Object> entityMap, ColumnHandler columnHandler) {
        String keyName = columnHandler.getColumnName();
        Object keyValue = entityMap.get(keyName);
        return this.getKeyValue(keyValue, columnHandler);
    }

    private String getEntityKey(Map<String, Object> entityMap) {
        String keyspace = this.cEntityDaoImpl.getKeyspace();
        String table = this.cEntityDaoImpl.getTable();
        List<ColumnHandler> keyHandlerList = this.cEntityDaoImpl.getKeyHandlerList();
        //
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append(keyspace).append(".").append(table);
        String keyValue;
        for (ColumnHandler columnHandler : keyHandlerList) {
            keyValue = this.getKeyValue(entityMap, columnHandler);
            stringBuilder.append("_").append(keyValue);
        }
        return stringBuilder.toString();
    }

    private String getEntityKey(Object[] keyValueArray) {
        String keyspace = this.cEntityDaoImpl.getKeyspace();
        String table = this.cEntityDaoImpl.getTable();
        List<ColumnHandler> keyHandlerList = this.cEntityDaoImpl.getKeyHandlerList();
        //
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append(keyspace).append(".").append(table);
        String keyValue;
        ColumnHandler columnHandler;
        for (int index = 0; index < keyHandlerList.size(); index++) {
            columnHandler = keyHandlerList.get(index);
            keyValue = this.getKeyValue(keyValueArray[index], columnHandler);
            stringBuilder.append("_").append(keyValue);
        }
        return stringBuilder.toString();
    }

    @Override
    public Object[] insert(Map<String, Object> entityMap) {
        return this.cEntityDaoImpl.insert(entityMap);
    }

    @Override
    public T insertAndInquire(Map<String, Object> entityMap) {
        T t = this.cEntityDaoImpl.insertAndInquire(entityMap);
        //加入缓存
        String entityKey = this.getEntityKey(entityMap);
        Element element = new Element(entityKey, t);
        this.cache.put(element);
        return t;
    }

    @Override
    public void batchInsert(List<Map<String, Object>> entityMapList) {
        this.cEntityDaoImpl.batchInsert(entityMapList);
    }

    @Override
    public Object[] update(Map<String, Object> entityMap) {
        Object[] keyValueArray = this.cEntityDaoImpl.update(entityMap);
        String entityKey = this.getEntityKey(entityMap);
        this.cache.remove(entityKey);
        return keyValueArray;
    }

    @Override
    public void batchUpdate(List<Map<String, Object>> entityMapList) {
        this.cEntityDaoImpl.batchUpdate(entityMapList);
        String entityKey;
        for (Map<String, Object> entityMap : entityMapList) {
            entityKey = this.getEntityKey(entityMap);
            this.cache.remove(entityKey);
        }
    }

    @Override
    public Object[] updateOrInsert(Map<String, Object> entityMap) {
        Object[] keyValueArray = this.cEntityDaoImpl.updateOrInsert(entityMap);
        String entityKey = this.getEntityKey(entityMap);
        this.cache.remove(entityKey);
        return keyValueArray;
    }

    @Override
    public T updateAndInquire(Map<String, Object> entityMap) {
        T t = this.cEntityDaoImpl.updateAndInquire(entityMap);
        //加入缓存
        String entityKey = this.getEntityKey(entityMap);
        Element element = new Element(entityKey, t);
        this.cache.put(element);
        return t;
    }

    @Override
    public void batchDelete(List<Object[]> keyValues) {
        this.cEntityDaoImpl.batchDelete(keyValues);
        String entityKey;
        for (Object[] keyValueArray : keyValues) {
            entityKey = this.getEntityKey(keyValueArray);
            this.cache.remove(entityKey);
        }
    }

    @Override
    public long count() {
        return this.cEntityDaoImpl.count();
    }

    @Override
    public List<T> query(String cql, Object... values) {
        return this.cEntityDaoImpl.query(cql, values);
    }

    @Override
    public ResultSet execute(String cql, Object... values) {
        return this.cEntityDaoImpl.execute(cql, values);
    }

    @Override
    public <S> void addSet(String columnName, S columnValue, Object... keyValue) {
        this.cEntityDaoImpl.addSet(columnName, columnValue, keyValue);
    }

    @Override
    public <S> void addSet(String columnName, Set<S> columnValues, Object... keyValue) {
        this.cEntityDaoImpl.addSet(columnName, columnValues, keyValue);
    }

    @Override
    public <S> void removeSet(String columnName, S columnValue, Object... keyValue) {
        this.cEntityDaoImpl.removeSet(columnName, columnValue, keyValue);
    }

    @Override
    public <S> void removeSet(String columnName, Set<S> columnValues, Object... keyValue) {
        this.cEntityDaoImpl.removeSet(columnName, columnValues, keyValue);
    }

    @Override
    public void clearSet(String columnName, Object... keyValue) {
        this.cEntityDaoImpl.clearSet(columnName, keyValue);
    }

    @Override
    public <S> Set<S> getSet(String columnName, Class<S> type, Object... keyValue) {
        return this.cEntityDaoImpl.getSet(columnName, type, keyValue);
    }

    @Override
    public <L> void addList(String columnName, L columnValue, Object... keyValue) {
        this.cEntityDaoImpl.addList(columnName, columnValue, keyValue);
    }

    @Override
    public <L> void addList(String columnName, List<L> columnValues, Object... keyValue) {
        this.cEntityDaoImpl.addList(columnName, columnValues, keyValue);
    }

    @Override
    public <L> void addFirstList(String columnName, L columnValue, Object... keyValue) {
        this.cEntityDaoImpl.addFirstList(columnName, columnValue, keyValue);
    }

    @Override
    public <L> void addFirstList(String columnName, List<L> columnValues, Object... keyValue) {
        this.cEntityDaoImpl.addFirstList(columnName, columnValues, keyValue);
    }

    @Override
    public <L> void removeList(String columnName, L columnValue, Object... keyValue) {
        this.cEntityDaoImpl.removeList(columnName, columnValue, keyValue);
    }

    @Override
    public <L> void removeList(String columnName, List<L> columnValues, Object... keyValue) {
        this.cEntityDaoImpl.removeList(columnName, columnValues, keyValue);
    }

    @Override
    public void clearList(String columnName, Object... keyValue) {
        this.cEntityDaoImpl.clearList(columnName, keyValue);
    }

    @Override
    public <L> List<L> getList(String columnName, Class<L> type, Object... keyValue) {
        return this.cEntityDaoImpl.getList(columnName, type, keyValue);
    }

    @Override
    public <K, V> void addMap(String columnName, K mapKeyValue, V mapValue, Object... keyValue) {
        this.cEntityDaoImpl.addMap(columnName, mapKeyValue, mapValue, keyValue);
    }

    @Override
    public <K, V> void addMap(String columnName, Map<K, V> maps, Object... keyValue) {
        this.cEntityDaoImpl.addMap(columnName, maps, keyValue);
    }

    @Override
    public <K, V> void removeMap(String columnName, K mapKeyValue, Object... keyValue) {
        this.cEntityDaoImpl.removeMap(columnName, mapKeyValue, keyValue);
    }

    @Override
    public <K, V> void removeMap(String columnName, List<K> mapKeyValues, Object... keyValue) {
        this.cEntityDaoImpl.removeMap(columnName, mapKeyValues, keyValue);
    }

    @Override
    public void clearMap(String columnName, Object... keyValue) {
        this.cEntityDaoImpl.clearMap(columnName, keyValue);
    }

    @Override
    public <K, V> Map<K, V> getMap(String columnName, Class<K> keyType, Class<V> valueType, Object... keyValue) {
        return this.cEntityDaoImpl.getMap(columnName, keyType, valueType, keyValue);
    }

    @Override
    public boolean exist(Object... keyValue) {
        return this.cEntityDaoImpl.exist(keyValue);
    }

    @Override
    public T inquireByKey(Object... keyValue) {
        T t;
        String entityKey = this.getEntityKey(keyValue);
        Element element = this.cache.getQuiet(entityKey);
        if (element != null) {
            t = (T) element.getObjectValue();
        } else {
            t = this.cEntityDaoImpl.inquireByKey(keyValue);
        }
        return t;
    }

    @Override
    public void delete(Object... keyValue) {
        this.cEntityDaoImpl.delete(keyValue);
        String entityKey = this.getEntityKey(keyValue);
        this.cache.remove(entityKey);
    }

    @Override
    public String check() {
        return this.cEntityDaoImpl.check();
    }

}
