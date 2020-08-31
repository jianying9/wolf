package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.Resource;
import com.wolf.framework.logger.LogFactory;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;

/**
 *
 * @author jianying9
 */
public class EsResourceImpl implements Resource {

    private final TransportClient transportClient;

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);

    public EsResourceImpl(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    @Override
    public void destory() {
        this.logger.info("elasticsearch client shutdown...");
        if (this.transportClient != null) {
            this.transportClient.close();
        }
    }
}
