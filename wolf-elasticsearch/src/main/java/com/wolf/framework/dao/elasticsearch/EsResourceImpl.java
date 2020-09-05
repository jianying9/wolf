package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.Resource;
import com.wolf.framework.logger.LogFactory;
import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;

/**
 *
 * @author jianying9
 */
public class EsResourceImpl implements Resource
{

    private final RestClient restClient;

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.DAO);

    public EsResourceImpl(RestClient restClient)
    {
        this.restClient = restClient;
    }

    @Override
    public void destory()
    {
        this.logger.info("elasticsearch client shutdown...");
        if (this.restClient != null) {
            try {
                this.restClient.close();
            } catch (IOException ex) {
            }
        }
    }
}
