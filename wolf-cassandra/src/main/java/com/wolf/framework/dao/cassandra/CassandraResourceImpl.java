package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.wolf.framework.context.Resource;

/**
 *
 * @author jianying9
 */
public class CassandraResourceImpl implements Resource {

    private final Session session;

    private final Cluster cluster;

    public CassandraResourceImpl(Session session, Cluster cluster) {
        this.session = session;
        this.cluster = cluster;
    }

    @Override
    public void destory() {
        this.session.close();
        this.cluster.close();
    }
}
