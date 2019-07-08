package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.Dao;
import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

/**
 * elasticsearch entity dao
 *
 * @author jianying9
 * @param <T>
 */
public interface EsEntityDao<T extends Entity> extends Dao {

    public String getIndex();

    public String getType();

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
     *
     * @param queryBuilder
     * @param from
     * @param size
     * @return
     */
    public List<T> search(QueryBuilder queryBuilder, int from, int size);

    /**
     *
     * @param queryBuilder
     * @param sort
     * @param from
     * @param size
     * @return
     */
    public List<T> search(QueryBuilder queryBuilder, SortBuilder sort, int from, int size);

    /**
     *
     * @param queryBuilder
     * @param sortList
     * @param from
     * @param size
     * @return
     */
    public List<T> search(QueryBuilder queryBuilder, List<SortBuilder> sortList, int from, int size);

    /**
     *
     * @param sort
     * @param from
     * @param size
     * @return
     */
    public List<T> search(SortBuilder sort, int from, int size);

    /**
     *
     * @param from
     * @param size
     * @return
     */
    public List<T> search(int from, int size);

    /**
     *
     * @param from
     * @param size
     * @return
     */
    public SearchResponse searcResponse(QueryBuilder queryBuilder, int from, int size);

    /**
     *
     * @param queryBuilder
     * @return
     */
    public List<T> search(QueryBuilder queryBuilder);

    /**
     * 删除
     *
     * @param keyValue
     */
    public void delete(Object keyValue);

    /**
     * 插入,返回keyValue
     *
     * @param entityMap
     * @return
     */
    public String insert(Map<String, Object> entityMap);

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
    public String update(Map<String, Object> entityMap);

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
     * 批量删除
     *
     * @param keyValues
     */
    public void batchDelete(List<Object> keyValues);

}
