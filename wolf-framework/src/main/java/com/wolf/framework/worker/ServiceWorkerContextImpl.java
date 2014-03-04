package com.wolf.framework.worker;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.service.parameter.ParametersContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class ServiceWorkerContextImpl implements ServiceWorkerContext {

    private final Map<String, String> existClassMap = new HashMap<String, String>(1024);
    private final ParametersContext parametersContext;
    private final Injecter injecter;
    private final ApplicationContext applicationContext;
    //服务集合
    private final Map<String, ServiceWorker> serviceWorkerMap;

    @Override
    public final void putServiceWorker(final String actionName, final ServiceWorker serviceWorker, String className) {
        if (this.serviceWorkerMap.containsKey(actionName)) {
            String existClassName = this.existClassMap.get(actionName);
            if (existClassName == null) {
                existClassName = "NULL";
            }
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("Error putting service worker. Cause: actionName reduplicated : ").append(actionName).append("\n").append("exist class : ").append(existClassName).append("\n").append("this class : ").append(className);
            throw new RuntimeException(errBuilder.toString());
        }
        this.serviceWorkerMap.put(actionName, serviceWorker);
        this.existClassMap.put(actionName, className);
    }

    /**
     * 构造函数
     *
     * @param properties
     */
    public ServiceWorkerContextImpl(
            final ParametersContext parametersContextBuilder,
            final Injecter injecter,
            final ApplicationContext applicationContext) {
        this.serviceWorkerMap = new HashMap<String, ServiceWorker>(256, 1);
        this.parametersContext = parametersContextBuilder;
        this.injecter = injecter;
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<String, ServiceWorker> getServiceWorkerMap() {
        return Collections.unmodifiableMap(this.serviceWorkerMap);
    }

    @Override
    public boolean assertExistServiceWorker(String actionName) {
        return this.serviceWorkerMap.containsKey(actionName);
    }

    @Override
    public ParametersContext getParametersContextBuilder() {
        return this.parametersContext;
    }

    @Override
    public Injecter getInjecter() {
        return this.injecter;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
