package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Dao;
import com.wolf.framework.dao.Entity;
import java.util.List;

/**
 * cassandra entity dao
 *
 * @author jianying9
 * @param <T>
 */
public interface CDao<T extends Entity> extends Dao {

    /**
     * 判断主键是否存在
     *
     * @param keyValue
     * @return
     */
    public boolean exist(Object... keyValue);

    /**
     * 根据主键查询
     *
     * @param keyValue
     * @return
     */
    public T inquireByKey(Object... keyValue);

    /**
     * 删除
     *
     * @param keyValue
     */
    public void delete(Object... keyValue);
    
    /**
     * 自定义cql查询
     *
     * @param cql
     * @param values
     * @return
     */
    public List<T> query(String cql, Object... values);

}
