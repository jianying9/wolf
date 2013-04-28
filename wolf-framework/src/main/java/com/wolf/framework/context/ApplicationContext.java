package com.wolf.framework.context;

import com.wolf.framework.worker.ServiceWorker;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.CacheManager;
import org.apache.derby.jdbc.EmbeddedSimpleDataSource;

/**
 *
 * @author aladdin
 */
public final class ApplicationContext {
    
    public final static ApplicationContext CONTEXT = new ApplicationContext() ;
    
    private boolean ready = false;
    
    private EmbeddedSimpleDataSource embeddedSimpleDataSource;
    
    private CacheManager cacheManager;
    
    private Map<String, ServiceWorker> serviceWorkerMap = new HashMap<String, ServiceWorker>(16, 1);

    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public ServiceWorker getServiceWorker(String act) {
        return this.serviceWorkerMap.get(act);
    }

    void setServiceWorkerMap(Map<String, ServiceWorker> serviceWorkerMap) {
        this.serviceWorkerMap.putAll(serviceWorkerMap);
    }
    
    public boolean isReady() {
        return this.ready;
    }
    
    void ready() {
        this.ready = true;
    }
    
    void setEmbeddedSimpleDataSource(EmbeddedSimpleDataSource embeddedSimpleDataSource) {
        this.embeddedSimpleDataSource = embeddedSimpleDataSource;
    }
    
    public void shutdownDatabase() {
        if (this.embeddedSimpleDataSource != null) {
            this.embeddedSimpleDataSource.setShutdownDatabase("shutdown");
            try {
                this.embeddedSimpleDataSource.getConnection();
            } catch (SQLException ex) {
            }
        }
    }
}
