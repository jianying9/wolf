package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体数据访问对象创建类
 *
 * @author jianying9
 * @param <T>
 */
public final class CCounterDaoBuilder<T extends Entity> {

    //表空间
    private final String keyspace;
    //table name
    private final String table;
    //key
    private final List<ColumnHandler> keyHandlerList;
    //column
    private final List<ColumnHandler> columnHandlerList;
    //实体class
    private final Class<T> clazz;
    //
    private final CassandraAdminContext<T> cassandraAdminContext;

    //
    public CCounterDaoBuilder(
            String keyspace,
            String tableName,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList,
            Class<T> clazz,
            CassandraAdminContext<T> cassandraAdminContext) {
        this.keyspace = keyspace;
        this.table = tableName;
        this.keyHandlerList = keyHandlerList;
        if (columnHandlerList == null) {
            this.columnHandlerList = new ArrayList(0);
        } else {
            this.columnHandlerList = columnHandlerList;
        }
        this.clazz = clazz;
        this.cassandraAdminContext = cassandraAdminContext;
    }

    public CCounterDao<T> build() {
        if (this.keyspace.isEmpty()) {
            throw new RuntimeException("Error when building CCounterDao. Cause: keyspace is empty");
        }
        if (this.table.isEmpty()) {
            throw new RuntimeException("Error when building CCounterDao. Cause: tableName is empty");
        }
        if (this.clazz == null) {
            throw new RuntimeException("Error when building CCounterDao. Cause: clazz is null");
        }
        if (this.keyHandlerList.isEmpty()) {
            throw new RuntimeException("Error when building CCounterDao. Cause: key is empty");
        }
        if (this.columnHandlerList.isEmpty()) {
            throw new RuntimeException("Error when building CCounterDao. Cause: column is empty");
        }
        //session
        final Session session = this.cassandraAdminContext.getSession();
        //
        CCounterDao<T> entityDao = new CCounterDaoImpl(
                session,
                keyspace,
                table,
                this.keyHandlerList,
                this.columnHandlerList,
                this.clazz
        );
        return entityDao;
    }
}
