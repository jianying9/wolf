package com.wolf.framework.dao;

import com.wolf.framework.dao.condition.InquireContext;
import java.util.List;
import java.util.Map;

/**
 * entity dao
 *
 * @author aladdin
 */
public interface EntityDao<T extends Entity> {

    /**
     * 根据主键查询
     *
     * @param key
     * @return
     */
    public T inquireByKey(final String keyValue);

    /**
     * 根据主键集合查询
     *
     * @param keyValues
     * @return
     */
    public List<T> inquireByKeys(final List<String> keyValues);
    
    
    /**
     * 单个条件查询,最多返回10行记录
     * @param columnName
     * @param columnValue
     * @return 
     */
    public List<T> inquireByColumn(final String columnName, final String columnValue);
    
    /**
     * 两个条件查询,最多返回10行记录
     * @param columnName
     * @param columnValue
     * @param columnNameTwo
     * @param columnValueTwo
     * @return 
     */
    
    public List<T> inquireByColumns(final String columnName, final String columnValue, final String columnNameTwo, final String columnValueTwo);
    
    /**
     * 复合条件分页查询,最多返回10行记录
     *
     * @param inquireContext
     * @return
     */
    public List<T> inquireByCondition(final InquireContext inquireContext);
    
    /**
     * 单个条件分页查询
     *
     * @param columnName 列名
     * @param columnValue 值
     * @return
     */
    public InquireResult<T> inquirePageByColumn(final String columnName, final String columnValue);

    /**
     * 两个条件分页查询
     *
     * @param columnName 列1
     * @param columnValue 值1
     * @param columnNameTwo 列2
     * @param columnValueTwo 值2
     * @return
     */
    public InquireResult<T> inquirePageByColumns(final String columnName, final String columnValue, final String columnNameTwo, final String columnValueTwo);

    /**
     * 复合条件分页查询
     *
     * @param inquireContext
     * @return
     */
    public InquireResult<T> inquirePageByCondition(final InquireContext inquireContext);

    /**
     * 插入,返回keyValue
     *
     * @param entityMap
     */
    public String insert(final Map<String, String> entityMap);
    
    /**
     * 插入，并返回新增实体
     * @param entityMap
     * @return 
     */
    public T insertAndInquire(final Map<String, String> entityMap);

    /**
     * 批量插入，无缓存
     *
     * @param entityMapList
     */
    public void batchInsert(final List<Map<String, String>> entityMapList);

    /**
     * 更新,返回keyValue
     *
     * @param entityMap
     */
    public String update(final Map<String, String> entityMap);

    /**
     * 批量更新
     *
     * @param entityMapList
     */
    public void batchUpdate(final List<Map<String, String>> entityMapList);

    /**
     * 更新并查询后新后值
     *
     * @param entityMap
     * @return
     */
    public T updateAndInquire(Map<String, String> entityMap);

    /**
     * 删除
     *
     * @param keyValue
     */
    public void delete(String keyValue);

    /**
     * 批量删除
     *
     * @param keyValues
     */
    public void batchDelete(final List<String> keyValues);
    
    /**
     * 单个条件查询主键集合,最多返回10行记录
     *
     * @param columnName
     * @param columnValue
     * @return
     */
    public List<String> inquireKeysByColumn(final String columnName, final String columnValue);
    
    /**
     * 两个条件查询主键集合,最多返回10行记录
     *
     * @param columnName 列1
     * @param columnValue 值1
     * @param columnNameTwo 列2
     * @param columnValueTwo 值2
     * @return
     */
    public List<String> inquireKeysByColumns(final String columnName, final String columnValue, final String columnNameTwo, final String columnValueTwo);
    
    /**
     * 复合条件查询主键集合,最多返回10行记录
     *
     * @param inquireContext 单态条件集合
     * @return
     */
    public List<String> inquireKeysByCondition(final InquireContext inquireContext);

    /**
     * 单个条件分页查询主键集合
     *
     * @param columnName
     * @param columnValue
     * @return
     */
    public InquireKeyResult inquirePageKeysByColumn(final String columnName, final String columnValue);

    /**
     * 两个条件分页查询主键集合
     *
     * @param columnName 列1
     * @param columnValue 值1
     * @param columnNameTwo 列2
     * @param columnValueTwo 值2
     * @return
     */
    public InquireKeyResult inquirePageKeysByColumns(final String columnName, final String columnValue, final String columnNameTwo, final String columnValueTwo);

    /**
     * 复合条件分页查询主键集合
     *
     * @param inquireContext 单态条件集合
     * @return
     */
    public InquireKeyResult inquirePageKeysByCondition(final InquireContext inquireContext);
    
    public int count(final String columnName, final String columnValue);
    
    public int count(final String columnName, final String columnValue, final String columnNameTwo, final String columnValueTwo);
    
    public int count(InquireContext inquireContext);
}
