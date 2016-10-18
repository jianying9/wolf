package com.wolf.framework.worker.build;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.service.parameter.ParameterContext;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.worker.ServiceWorker;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface WorkerBuildContext {

    public void putServiceWorker(final String actionName, final ServiceWorker serviceWorker, String className);

    public Map<String, ServiceWorker> getServiceWorkerMap();

    public boolean assertExistServiceWorker(final String actionName);

    public Injecter getInjecter();
    
    public ParameterContext getParameterContext();
    
    public ApplicationContext getApplicationContext();
    
    public RequestParameterHandler getNextIndexHandler();
    
    public RequestParameterHandler getNextSizeHandler();
}
