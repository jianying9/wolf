package com.wolf.framework.dao;

import com.wolf.framework.dao.condition.InquirePageContext;
import com.wolf.framework.dao.condition.InquireIndexPageContext;
import java.util.List;
import java.util.Map;

/**
 * redis entity dao
 *
 * @author aladdin
 */
public interface REntityDao<T extends Entity> {

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
     * 插入,返回keyValue
     *
     * @param entityMap
     */
    public String insert(final Map<String, String> entityMap);

    /**
     * 插入，并返回新增实体
     *
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
     * 更新并返回更新结果
     *
     * @param entityMap
     * @return
     */
    public T updateAndInquire(Map<String, String> entityMap);

    /**
     * 更新并查询后新后值
     *
     * @param entityMap
     * @return
     */
//    public T updateAndInquire(Map<String, String> entityMap);
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
     * 根据得分正序全表分页查询主键
     *
     * @param inquirePageContext
     * @return
     */
    public List<String> inquireKeys(InquirePageContext inquirePageContext);

    /**
     * 根据得分倒序全表分页查询主键
     *
     * @param inquirePageContext
     * @return
     */
    public List<String> inquireKeysDESC(InquirePageContext inquirePageContext);

    /**
     * 根据得分正序全表分页查询
     *
     * @param inquirePageContext
     * @return
     */
    public List<T> inquire(InquirePageContext inquirePageContext);

    /**
     * 根据得分倒序全表分页查询
     *
     * @param inquirePageContext
     * @return
     */
    public List<T> inquireDESC(InquirePageContext inquirePageContext);

    /**
     * 统计全表总记录
     *
     * @return
     */
    public long count();

    /**
     * 根据得分正序索引条件查询主键集合
     *
     * @param inquireRedisIndexContext
     * @return
     */
    public List<String> inquireKeysByIndex(InquireIndexPageContext inquireRedisIndexContext);

    /**
     * 根据得分倒序索引条件查询主键集合
     *
     * @param inquireRedisIndexContext
     * @return
     */
    public List<String> inquireKeysByIndexDESC(InquireIndexPageContext inquireRedisIndexContext);

    /**
     * 根据得分正序索引条件查询
     *
     * @param inquireRedisIndexContext
     * @return
     */
    public List<T> inquireByIndex(InquireIndexPageContext inquireRedisIndexContext);

    /**
     * 根据得分倒序索引条件查询
     *
     * @param inquireRedisIndexContext
     * @return
     */
    public List<T> inquireByIndexDESC(InquireIndexPageContext inquireRedisIndexContext);

    /**
     * 索引统计
     *
     * @param indexName
     * @param indexValue
     * @return
     */
    public long countByIndex(final String indexName, final String indexValue);

    /**
     * 增加某number类型且非索引列的值
     *
     * @param keyValue
     * @param columnName
     * @param value
     * @return
     */
    public long increase(String keyValue, String columnName, long value);

    /**
     * 设置当前记录的key索引得分,影响key分页查询的排序
     *
     * @param sorce
     */
    public void setKeySorce(Map<String, String> entityMap, long sorce);
    
    /**
     * 更新key索引的得分
     * @param keyValue
     * @param sorce 
     */
    public void updateKeySorce(String keyValue, long sorce);

    /**
     * 设置当前记录的列索引得分，影响index分页查询的排序
     *
     * @param columnName
     * @param columnValue
     * @param sorce
     */
    public void setIndexSorce(Map<String, String> entityMap, String columnName, long sorce);
}
