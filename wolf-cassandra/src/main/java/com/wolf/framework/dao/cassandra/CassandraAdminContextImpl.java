package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.Session;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.Resource;
import com.wolf.framework.dao.Entity;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class CassandraAdminContextImpl implements CassandraAdminContext {

    private final Map<Class, CassandraHandler> cassandraHandlerMap = new HashMap<Class, CassandraHandler>(2, 1);

    private final Session session;

    private final Cluster cluster;

    public CassandraAdminContextImpl(ApplicationContext applicationContext) {
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
    public <T extends Entity> CassandraHandler getCassandraHandler(Class<T> clazz) {
        return this.cassandraHandlerMap.get(clazz);
    }

    @Override
    public <T extends Entity> void putCassandraHandler(Class<T> clazz, CassandraHandler cassandraHandler, String keyspace, String table) {
        if (this.cassandraHandlerMap.containsKey(clazz)) {
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("Error putting CassandraHandler. Cause: keyspace.table duplicated : ")
                    .append(keyspace).append('(').append(table).append(")\n");
            throw new RuntimeException(errBuilder.toString());
        }
        this.cassandraHandlerMap.put(clazz, cassandraHandler);
    }

    @Override
    public Session getSession() {
        return this.session;
    }
}
