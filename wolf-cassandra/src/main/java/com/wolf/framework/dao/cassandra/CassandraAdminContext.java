package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Session;
import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface CassandraAdminContext<T extends Entity> {
    
    public void putCEntityDao(final Class<T> clazz, final CEntityDao<T> cEntityDao, String keyspace, String table);
    
    public CEntityDao<T> getCEntityDao(final Class<T> clazz);
    
    public Map<Class, CEntityDao<T>> getCEntityDao();
    
    public void putCCounterDao(final Class<T> clazz, final CCounterDao<T> cCounterDao, String keyspace, String table);
    
    public CCounterDao<T> getCCounterDao(final Class<T> clazz);
    
    public Map<Class, CCounterDao<T>> getCCounterDao();
    
    public Session getSession();
}
