package com.wolf.framework.worker.build;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.interceptor.Interceptor;
import com.wolf.framework.interceptor.InterceptorContext;
import com.wolf.framework.service.parameter.ServiceExtendContext;
import com.wolf.framework.service.parameter.ServicePushContext;
import com.wolf.framework.worker.ServiceWorker;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局信息构造类
 *
 * @author aladdin
 */
public class WorkerBuildContextImpl implements WorkerBuildContext {

    private final Map<String, String> existClassMap = new HashMap<>(1024);
    private final Injecter injecter;
    private final ApplicationContext applicationContext;
    private final List<Interceptor> interceptorList;
    private final ServiceExtendContext serviceExtendContext;
    private final ServicePushContext servicePushContext;
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
     * @param serviceExtendContext
     * @param servicePushContext
     * @param injecter
     * @param interceptorContext
     * @param applicationContext
     */
    public WorkerBuildContextImpl(
            ServiceExtendContext serviceExtendContext,
            ServicePushContext servicePushContext,
            Injecter injecter,
            InterceptorContext interceptorContext,
            ApplicationContext applicationContext) {
        this.serviceWorkerMap = new HashMap<>(2, 1);
        this.serviceExtendContext = serviceExtendContext;
        this.servicePushContext = servicePushContext;
        this.injecter = injecter;
        this.applicationContext = applicationContext;
        this.interceptorList = interceptorContext.getInterceptorList();
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
    public Injecter getInjecter() {
        return this.injecter;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public List<Interceptor> getInterceptorList() {
        return this.interceptorList;
    }

    @Override
    public ServiceExtendContext getServiceExtendContext() {
        return this.serviceExtendContext;
    }

    @Override
    public ServicePushContext getServicePushContext() {
        return this.servicePushContext;
    }
}
