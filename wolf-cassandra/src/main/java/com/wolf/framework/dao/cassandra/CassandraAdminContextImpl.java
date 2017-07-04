package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.Session;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.Resource;
import com.wolf.framework.dao.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class CassandraAdminContextImpl<T extends Entity> implements CassandraAdminContext<T> {
    
    private static CassandraAdminContextImpl INSTANCE = null;
    
    public static CassandraAdminContextImpl getInstance(ApplicationContext applicationContext) {
        synchronized(CassandraAdminContextImpl.class) {
            if(INSTANCE == null) {
                INSTANCE = new CassandraAdminContextImpl(applicationContext);
            }
        }
        return INSTANCE;
    }

    private final Map<Class, CEntityDao<T>> cEntityDaoMap = new HashMap<>(2, 1);
    private final Map<Class, CCounterDao<T>> cCounterDaoMap = new HashMap<>(2, 1);

    private final Session session;

    private final Cluster cluster;

    private CassandraAdminContextImpl(ApplicationContext applicationContext) {
        final String point = applicationContext.getParameter(CassandraConfig.CASSANDRA_CONTACT_POINT);
        final String userName = applicationContext.getParameter(CassandraConfig.CASSANDRA_USERNAME);
        final String password = applicationContext.getParameter(CassandraConfig.CASSANDRA_PASSWORD);
        this.cluster = Cluster.builder()
                .addContactPoint(point)
                .withCredentials(userName, password) 
                .build();
        this.cluster.getConfiguration()
                .getProtocolOptions()
                .setCompression(ProtocolOptions.Compression.LZ4);
        this.session = cluster.connect();
        Resource resource = new CassandraResourceImpl(this.session, this.cluster);
        applicationContext.addResource(resource);
    }
    
    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public void putCEntityDao(Class<T> clazz, CEntityDao<T> cEntityDao, String keyspace, String table) {
        if (this.cEntityDaoMap.containsKey(clazz)) {
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("Error putting CassandraHandler. Cause: keyspace.table duplicated : ")
                    .append(keyspace).append('(').append(table).append(")\n");
            throw new RuntimeException(errBuilder.toString());
        }
        this.cEntityDaoMap.put(clazz, cEntityDao);
    }

    @Override
    public CEntityDao<T> getCEntityDao(Class<T> clazz) {
        return this.cEntityDaoMap.get(clazz);
    }

    @Override
    public void putCCounterDao(Class<T> clazz, CCounterDao<T> cCounterDao, String keyspace, String table) {
        if (this.cCounterDaoMap.containsKey(clazz)) {
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("Error putting CassandraHandler. Cause: keyspace.table duplicated : ")
                    .append(keyspace).append('(').append(table).append(")\n");
            throw new RuntimeException(errBuilder.toString());
        }
        this.cCounterDaoMap.put(clazz, cCounterDao);
    }

    @Override
    public CCounterDao<T> getCCounterDao(Class<T> clazz) {
        return this.cCounterDaoMap.get(clazz);
    }

    @Override
    public Map<Class, CEntityDao<T>> getCEntityDao() {
        return Collections.unmodifiableMap(this.cEntityDaoMap);
    }

    @Override
    public Map<Class, CCounterDao<T>> getCCounterDao() {
        return Collections.unmodifiableMap(this.cCounterDaoMap);
    }
}
