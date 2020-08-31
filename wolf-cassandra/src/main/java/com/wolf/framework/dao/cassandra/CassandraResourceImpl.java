package com.wolf.framework.dao.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.Resource;
import com.wolf.framework.logger.LogFactory;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jianying9
 */
public class CassandraResourceImpl implements Resource {

    private final Session session;

    private final Cluster cluster;
    
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);

    public CassandraResourceImpl(Session session, Cluster cluster) {
        this.session = session;
        this.cluster = cluster;
    }

    @Override
    public void destory() {
        this.logger.info("cassandra client shutdown...");
        this.session.close();
        this.cluster.close();
    }
}
