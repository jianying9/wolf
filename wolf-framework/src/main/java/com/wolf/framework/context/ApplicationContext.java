package com.wolf.framework.context;

import com.wolf.framework.comet.CometContext;
import com.wolf.framework.comet.CometContextImpl;
import com.wolf.framework.local.Local;
import com.wolf.framework.worker.ServiceWorker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class ApplicationContext {

    public final static ApplicationContext CONTEXT = new ApplicationContext();
    private boolean ready = false;
    private Map<String, String> parameterMap;
    private final Map<String, ServiceWorker> serviceWorkerMap = new HashMap<String, ServiceWorker>(2, 1);
    private final Map<Class<? extends Local>, Local> localServiceMap = new HashMap<Class<? extends Local>, Local>(2, 1);
    private final List<Resource> resourceList = new ArrayList<Resource>(2);
    private final CometContext cometContext = new CometContextImpl();

    public Map<String, ServiceWorker> getServiceWorkerMap() {
        return Collections.unmodifiableMap(this.serviceWorkerMap);
    }

    public ServiceWorker getServiceWorker(String act) {
        return this.serviceWorkerMap.get(act);
    }

    void setServiceWorkerMap(Map<String, ServiceWorker> serviceWorkerMap) {
        this.serviceWorkerMap.putAll(serviceWorkerMap);
    }

    public <L extends Local> L getLocalService(Class<? extends Local> clazz) {
        return (L)this.localServiceMap.get(clazz);
    }

    void setLocalServiceMap(Map<Class<? extends Local>, Local> localServiceMap) {
        this.localServiceMap.putAll(localServiceMap);
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

    public CometContext getCometContext() {
        return cometContext;
    }
}
