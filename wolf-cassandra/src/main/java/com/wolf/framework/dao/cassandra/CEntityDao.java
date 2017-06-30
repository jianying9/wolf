package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.ResultSet;
import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * cassandra entity dao
 *
 * @author jianying9
 * @param <T>
 */
public interface CEntityDao<T extends Entity> extends CDao<T> {

    /**
     * 插入,返回keyValue
     *
     * @param entityMap
     * @return
     */
    public Object[] insert(Map<String, Object> entityMap);

    /**
     * 插入，并返回新增实体
     *
     * @param entityMap
     * @return
     */
    public T insertAndInquire(Map<String, Object> entityMap);

    /**
     * 批量插入，无缓存
     *
     * @param entityMapList
     */
    public void batchInsert(List<Map<String, Object>> entityMapList);

    /**
     * 更新,返回keyValue
     *
     * @param entityMap
     * @return
     */
    public Object[] update(Map<String, Object> entityMap);

    /**
     * 批量更新
     *
     * @param entityMapList
     */
    public void batchUpdate(List<Map<String, Object>> entityMapList);

    /**
     * 更新或新增,返回keyValue
     *
     * @param entityMap
     * @return
     */
    public Object[] updateOrInsert(Map<String, Object> entityMap);

    /**
     * 更新并返回更新结果
     *
     * @param entityMap
     * @return
     */
    public T updateAndInquire(Map<String, Object> entityMap);

    /**
     * 批量删除
     *
     * @param keyValues
     */
    public void batchDelete(List<Object[]> keyValues);

    /**
     * 统计全表总记录
     *
     * @return
     */
    public long count();

    /**
     * 自定义cql查询
     *
     * @param cql
     * @param values
     * @return
     */
    public List<T> query(String cql, Object... values);

    /**
     * 自定义cql执行
     *
     * @param cql
     * @param values
     * @return
     */
    public ResultSet execute(String cql, Object... values);

    public <S extends Object> void addSet(String columnName, S columnValue, Object... keyValue);

    public <S extends Object> void addSet(String columnName, Set<S> columnValues, Object... keyValue);

    public <S extends Object> void removeSet(String columnName, S columnValue, Object... keyValue);

    public <S extends Object> void removeSet(String columnName, Set<S> columnValues, Object... keyValue);

    public void clearSet(String columnName, Object... keyValue);

    public <S extends Object> Set<S> getSet(String columnName, Class<S> type, Object... keyValue);

    public <L extends Object> void addList(String columnName, L columnValue, Object... keyValue);

    public <L extends Object> void addList(String columnName, List<L> columnValues, Object... keyValue);

    public <L extends Object> void addFirstList(String columnName, L columnValue, Object... keyValue);

    public <L extends Object> void addFirstList(String columnName, List<L> columnValues, Object... keyValue);

    public <L extends Object> void removeList(String columnName, L columnValue, Object... keyValue);

    public <L extends Object> void removeList(String columnName, List<L> columnValues, Object... keyValue);

    public void clearList(String columnName, Object... keyValue);

    public <L extends Object> List<L> getList(String columnName, Class<L> type, Object... keyValue);

    public <K extends Object, V extends Object> void addMap(String columnName, K mapKeyValue, V mapValue, Object... keyValue);

    public <K extends Object, V extends Object> void addMap(String columnName, Map<K, V> maps, Object... keyValue);

    public <K extends Object, V extends Object> void removeMap(String columnName, K mapKeyValue, Object... keyValue);

    public <K extends Object, V extends Object> void removeMap(String columnName, List<K> mapKeyValues, Object... keyValue);

    public void clearMap(String columnName, Object... keyValue);

    public <K extends Object, V extends Object> Map<K, V> getMap(String columnName, Class<K> keyType, Class<V> valueType, Object... keyValue);

}
