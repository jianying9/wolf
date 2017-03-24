package com.wolf.framework.worker.build;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.interceptor.Interceptor;
import com.wolf.framework.service.parameter.ServiceExtend;
import com.wolf.framework.worker.ServiceWorker;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface WorkerBuildContext {

    public void putServiceWorker(final String actionName, final ServiceWorker serviceWorker, String className);

    public Map<String, ServiceWorker> getServiceWorkerMap();

    public boolean assertExistServiceWorker(final String actionName);

    public Injecter getInjecter();
    
    public ApplicationContext getApplicationContext();
    
    public List<Interceptor> getInterceptorList();
    
    public ServiceExtend getServiceExtend();
    
}
