package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.ResultSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface CassandraHandler {

    /**
     * 判断某个主键值是否存在
     *
     * @param keyValue
     * @return
     */
    public boolean exist(Object... keyValue);

    /**
     * 根据主键查询一行记录
     *
     * @param keyValue
     * @return
     */
    public Map<String, Object> queryByKey(Object... keyValue);

    /**
     * 插入一行记录
     *
     * @param entityMap
     * @return
     */
    public Object[] insert(Map<String, Object> entityMap);

    /**
     * 批量插入
     *
     * @param entityMapList
     */
    public void batchInsert(List<Map<String, Object>> entityMapList);

    /**
     * 更新一行记录
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
     * 删除一行记录
     *
     * @param keyValue
     */
    public void delete(Object... keyValue);

    /**
     * 批量删除
     *
     * @param keyValues
     */
    public void batchDelete(List<Object[]> keyValues);

    /**
     * 查询总记录数
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
     * 自定义cql查询
     * @param cql
     * @param values
     * @return 
     */
    public List<Map<String, Object>> query(String cql, Object... values);

    /**
     * 自定义sql查询
     *
     * @param cql
     * @param values
     * @return
     */
    public ResultSet execute(String cql, Object... values);
}
