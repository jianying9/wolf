package com.wolf.framework.worker;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.service.parameter.ParameterContext;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface ServiceWorkerContext {

    public void putServiceWorker(final String actionName, final ServiceWorker serviceWorker, String className);

    public Map<String, ServiceWorker> getServiceWorkerMap();

    public boolean assertExistServiceWorker(final String actionName);

    public Injecter getInjecter();
    
    public ParameterContext getParameterContext();
    
    public ApplicationContext getApplicationContext();
}
