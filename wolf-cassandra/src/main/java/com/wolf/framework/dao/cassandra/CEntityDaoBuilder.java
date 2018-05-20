package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Session;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.Cache;

/**
 * 实体数据访问对象创建类
 *
 * @author jianying9
 * @param <T>
 */
public final class CEntityDaoBuilder<T extends Entity> {

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

    private final boolean cache;

    private final Cache escache;
    //
    private final CassandraAdminContext<T> cassandraAdminContext;

    //
    public CEntityDaoBuilder(
            String keyspace,
            String tableName,
            List<ColumnHandler> keyHandlerList,
            List<ColumnHandler> columnHandlerList,
            Class<T> clazz,
            CassandraAdminContext<T> cassandraAdminContext,
            boolean cache,
            Cache escache
    ) {
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
        this.cache = cache;
        this.escache = escache;
    }

    public CEntityDao<T> build() {
        if (this.keyspace.isEmpty()) {
            throw new RuntimeException("Error when building CEntityDao. Cause: keyspace is empty");
        }
        if (this.table.isEmpty()) {
            throw new RuntimeException("Error when building CEntityDao. Cause: tableName is empty");
        }
        if (this.clazz == null) {
            throw new RuntimeException("Error when building CEntityDao. Cause: clazz is null");
        }
        if (this.keyHandlerList.isEmpty()) {
            throw new RuntimeException("Error when building CEntityDao. Cause: key is empty");
        }
        //session
        final Session session = this.cassandraAdminContext.getSession();
        //
        CEntityDaoImpl<T> CEntityDaoImpl = new CEntityDaoImpl(
                session,
                this.keyspace,
                this.table,
                this.keyHandlerList,
                this.columnHandlerList,
                this.clazz
        );
        CEntityDao<T> entityDao = CEntityDaoImpl;
        if (this.cache && this.escache != null) {
            //启用缓存
            entityDao = new CEntityCacheDaoImpl(this.escache, CEntityDaoImpl);
        }
        return entityDao;
    }
}
