package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Session;
import com.wolf.framework.dao.Entity;

/**
 *
 * @author jianying9
 */
public interface CassandraAdminContext {
    
    public <T extends Entity> void putCassandraHandler(final Class<T> clazz, final CassandraHandler cassandraHandler, final String keyspace, String table);
    
    public <T extends Entity> CassandraHandler getCassandraHandler(final Class<T> clazz);
    
    public Session getSession();
}
