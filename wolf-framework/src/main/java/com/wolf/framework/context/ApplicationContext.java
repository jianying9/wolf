package com.wolf.framework.context;

import com.wolf.framework.worker.ServiceWorker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class ApplicationContext {

    public final static String REDIS_HOST = "redisHost";
    public final static ApplicationContext CONTEXT = new ApplicationContext();
    private boolean ready = false;
    private Map<String, String> parameterMap;
    private Map<String, ServiceWorker> serviceWorkerMap = new HashMap<String, ServiceWorker>(16, 1);
    private final List<Resource> resourceList = new ArrayList<Resource>(2);

    public ServiceWorker getServiceWorker(String act) {
        return this.serviceWorkerMap.get(act);
    }

    void setServiceWorkerMap(Map<String, ServiceWorker> serviceWorkerMap) {
        this.serviceWorkerMap.putAll(serviceWorkerMap);
    }

    public String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public boolean isReady() {
        return this.ready;
    }

    void ready() {
        this.ready = true;
    }

    public void addResource(Resource resource) {
        this.resourceList.add(resource);
    }

    public void contextDestroyed() {
        for (Resource resource : this.resourceList) {
            resource.destory();
        }
    }
}
