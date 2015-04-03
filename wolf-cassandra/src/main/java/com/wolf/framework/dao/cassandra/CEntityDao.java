package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.ResultSet;
import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;

/**
 * cassandra entity dao
 *
 * @author jianying
 * @param <T>
 */
public interface CEntityDao<T extends Entity> {

    /**
     * 判断主键是否存在
     *
     * @param keyValue
     * @return
     */
    public boolean exist(Object keyValue);

    /**
     * 根据主键查询
     *
     * @param keyValue
     * @return
     */
    public T inquireByKey(Object keyValue);
    
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
     * 更新并返回更新结果
     *
     * @param entityMap
     * @return
     */
    public T updateAndInquire(Map<String, Object> entityMap);

    /**
     * 删除
     *
     * @param keyValue
     */
    public void delete(Object keyValue);

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
     * 自增
     *
     * @param columnName
     * @param value
     * @param keyValue
     * @return
     */
    public long increase(String columnName, long value, Object... keyValue);
    
    /**
     * 自定义sql查询
     * @param cql
     * @param values
     * @return 
     */
    public ResultSet execute(String cql, Object... values);
}
