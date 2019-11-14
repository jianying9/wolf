package com.wolf.framework.context;

import com.wolf.framework.push.PushContextImpl;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.service.parameter.PushInfo;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterFactoryImpl;
import com.wolf.framework.worker.ServiceWorker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wolf.framework.push.PushContext;
import net.sf.ehcache.Cache;

/**
 *
 * @author aladdin
 */
public final class ApplicationContext {

    public final static ApplicationContext CONTEXT = new ApplicationContext();
    private boolean ready = false;
    private Map<String, String> parameterMap;
    private final Map<String, ServiceWorker> serviceWorkerMap = new HashMap(2, 1);
    private final Map<String, PushInfo> pushInfoMap = new HashMap(2, 1);
    private final List<Resource> resourceList = new ArrayList(2);
    private final PushContext pushContext = new PushContextImpl();
    private String appContextPath = "/";
    private final FilterFactory filterFactory = new FilterFactoryImpl();
    private Cache cache;

    private Map<Class<?>, List<ColumnHandler>> entityInfoMap;

    public List<ColumnHandler> getEntityInfo(Class<? extends Entity> clazz) {
        return this.entityInfoMap.get(clazz);
    }

    void setEntityInfo(Map<Class<?>, List<ColumnHandler>> entityInfoMap) {
        this.entityInfoMap = entityInfoMap;
    }

    public FilterFactory getFilterFactory() {
        return this.filterFactory;
    }

    public Map<String, ServiceWorker> getServiceWorkerMap() {
        return Collections.unmodifiableMap(this.serviceWorkerMap);
    }

    public Map<String, PushInfo> getPushInfoMap() {
        return Collections.unmodifiableMap(this.pushInfoMap);
    }

    void setPushInfoMap(Map<String, PushInfo> pushInfoMap) {
        this.pushInfoMap.putAll(pushInfoMap);
    }

    public ServiceWorker getServiceWorker(String route) {
        return this.serviceWorkerMap.get(route);
    }

    void setServiceWorkerMap(Map<String, ServiceWorker> serviceWorkerMap) {
        this.serviceWorkerMap.putAll(serviceWorkerMap);
    }

    public String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    public void setParameter(String name, String value) {
        this.parameterMap.put(name, value);
    }

    void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    void setCache(Cache cache) {
        this.cache = cache;
    }

    public Cache getCache() {
        return this.cache;
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

    void contextDestroyed() {
        for (Resource resource : this.resourceList) {
            resource.destory();
            
        }
    }

    public PushContext getPushContext() {
        return pushContext;
    }

    public String getAppContextPath() {
        return appContextPath;
    }

    void setAppContextPath(String appContextPath) {
        this.appContextPath = appContextPath;
    }
}
