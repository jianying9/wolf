package com.wolf.framework.dao.cassandra;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public interface CassandraHandler {
    
    /**
     * 判断某个主键值是否存在
     * @param keyValue
     * @return 
     */
    public boolean exist(Object... keyValue);
    
    /**
     * 根据主键查询一行记录
     * @param keyValue
     * @return 
     */
    public Map<String, Object> inquireByKey(Object... keyValue);
    
    /**
     * 插入一行记录
     * @param entityMap
     * @return 
     */
    public Object[] insert(Map<String, Object> entityMap);
    
    /**
     * 批量插入
     * @param entityMapList 
     */
    public void batchInsert(List<Map<String, Object>> entityMapList);
    
    /**
     * 更新一行记录
     * @param entityMap
     * @return 
     */
    public Object[] update(Map<String, Object> entityMap);
    
    /**
     * 批量更新
     * @param entityMapList 
     */
    public void batchUpdate(List<Map<String, Object>> entityMapList);
    
    /**
     * 删除一行记录
     * @param keyValue 
     */
    public void delete(Object... keyValue);
    
    /**
     * 批量删除
     * @param keyValues 
     */
    public void batchDelete(List<Object[]> keyValues);
    
    /**
     * 查询总记录数
     * @return 
     */
    public long count();

    public void addSet(String keyValue, String columnName, String value);

    public void addSet(String keyValue, String columnName, Set<String> values);

    public void removeSet(String keyValue, String columnName, String value);

    public void removeSet(String keyValue, String columnName, Set<String> values);

    public void clearSet(String keyValue, String columnName);

    public Set<String> getSet(String keyValue, String columnName);

    public void addList(String keyValue, String columnName, String value);

    public void addList(String keyValue, String columnName, List<String> values);

    public void addFirstList(String keyValue, String columnName, String value);

    public void addFirstList(String keyValue, String columnName, List<String> values);

    public void removeList(String keyValue, String columnName, String value);

    public void removeList(String keyValue, String columnName, List<String> values);

    public void clearList(String keyValue, String columnName);

    public List<String> getList(String keyValue, String columnName);

    public void addMap(String keyValue, String columnName, String mapKeyValue, String mapValue);

    public void addMap(String keyValue, String columnName, Map<String, String> values);

    public void removeMap(String keyValue, String columnName, String mapKeyValue);

    public void removeMap(String keyValue, String columnName, Set<String> mapKeyValues);

    public void clearMap(String keyValue, String columnName);

    public Map<String, String> getMap(String keyValue, String columnName);
    
    public long increase(String columnName, long value, Object... keyValue);
}
