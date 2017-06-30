package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Entity;

/**
 * cassandra entity dao
 *
 * @author jianying9
 * @param <T>
 */
public interface CCounterDao<T extends Entity> extends CDao<T> {

    /**
     * 自增
     *
     * @param columnName
     * @param value
     * @param keyValue
     * @return
     */
    public long increase(String columnName, long value, Object... keyValue);
}
