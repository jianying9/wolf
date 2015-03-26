package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体数据访问对象创建类
 *
 * @author aladdin
 * @param <T>
 */
public final class CEntityDaoBuilder<T extends Entity> {

    //表空间
    private final String keyspace;
    //table name
    private final String table;
    //是否是counter
    private final boolean counter;
    //key
    private final ColumnHandler keyHandler;
    //column
    private final List<ColumnHandler> columnHandlerList;
    //实体class
    private final Class<T> clazz;
    private final CEntityDaoContext<T> entityDaoContext;

    public CEntityDaoBuilder(String keyspace, String tableName, boolean counter, ColumnHandler keyHandler, List<ColumnHandler> columnHandlerList, Class<T> clazz, CEntityDaoContext<T> entityDaoContext) {
        this.keyspace = keyspace;
        this.table = tableName;
        this.counter = counter;
        this.keyHandler = keyHandler;
        if (columnHandlerList == null) {
            this.columnHandlerList = new ArrayList<ColumnHandler>(0);
        } else {
            this.columnHandlerList = columnHandlerList;
        }
        this.clazz = clazz;
        this.entityDaoContext = entityDaoContext;
    }

    public CEntityDao<T> build() {
        if (this.keyspace.isEmpty()) {
            throw new RuntimeException("Error when building CEntityDao. Cause: tableName is empty");
        }
        if (this.table.isEmpty()) {
            throw new RuntimeException("Error when building CEntityDao. Cause: tableName is empty");
        }
        if (this.clazz == null) {
            throw new RuntimeException("Error when building CEntityDao. Cause: clazz is null");
        }
        if (this.keyHandler == null) {
            throw new RuntimeException("Error when building CEntityDao. Cause: key is null");
        }
        
        CEntityDao<T> entityDao = new CEntityDaoImpl(
                null,
                null,
                null,
                null);
        return entityDao;
    }
}
