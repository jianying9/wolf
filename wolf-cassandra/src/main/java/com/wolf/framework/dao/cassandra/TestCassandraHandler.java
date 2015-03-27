package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class TestCassandraHandler {

    static CassandraAdminContext cassandraAdminContext;

    /**
     * 清空某张表
     *
     * @param <T>
     * @param clazz
     */
    public static <T extends Entity> void truncate(Class<T> clazz) {
    }

    /**
     * 往指定的表插入某行记录
     *
     * @param <T>
     * @param clazz
     * @param entityMap
     */
    public static <T extends Entity> void insert(Class<T> clazz, Map<String, String> entityMap) {
        CassandraHandler cassandraHandler = cassandraAdminContext.getCassandraHandler(clazz);
        if (cassandraHandler != null) {
            cassandraHandler.insert(entityMap);
        }
    }

    /**
     * 在指定的表中删除某行记录
     *
     * @param <T>
     * @param clazz
     * @param keyValue
     */
    public static <T extends Entity> void delete(Class<T> clazz, String keyValue) {
        CassandraHandler cassandraHandler = cassandraAdminContext.getCassandraHandler(clazz);
        if (cassandraHandler != null) {
            cassandraHandler.delete(keyValue);
        }
    }
}
