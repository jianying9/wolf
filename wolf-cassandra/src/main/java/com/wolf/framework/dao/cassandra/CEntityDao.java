package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.condition.InquirePageContext;
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
    public boolean exist(String keyValue);

    /**
     * 根据主键查询
     *
     * @param keyValue
     * @return
     */
    public T inquireByKey(String keyValue);

    /**
     * 根据主键集合查询
     *
     * @param keyValues
     * @return
     */
    public List<T> inquireByKeys(List<String> keyValues);

    /**
     * 插入,返回keyValue
     *
     * @param entityMap
     * @return
     */
    public String insert(Map<String, String> entityMap);

    /**
     * 插入，并返回新增实体
     *
     * @param entityMap
     * @return
     */
    public T insertAndInquire(Map<String, String> entityMap);

    /**
     * 批量插入，无缓存
     *
     * @param entityMapList
     */
    public void batchInsert(List<Map<String, String>> entityMapList);

    /**
     * 更新,返回keyValue
     *
     * @param entityMap
     * @return
     */
    public String update(Map<String, String> entityMap);

    /**
     * 批量更新
     *
     * @param entityMapList
     */
    public void batchUpdate(List<Map<String, String>> entityMapList);

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
    public void batchDelete(List<String> keyValues);

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
}
