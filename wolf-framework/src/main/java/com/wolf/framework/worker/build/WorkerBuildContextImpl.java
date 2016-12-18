package com.wolf.framework.worker.build;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.interceptor.Interceptor;
import com.wolf.framework.interceptor.InterceptorContext;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.service.parameter.request.LongRequestParameterHandlerImpl;
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
    private final RequestParameterHandler nextIndexHandler;
    private final RequestParameterHandler nextSizeHandler;
    private final List<Interceptor> interceptorList;
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
     * @param injecter
     * @param interceptorContext
     * @param applicationContext
     */
    public WorkerBuildContextImpl(
            final Injecter injecter,
            final InterceptorContext interceptorContext,
            final ApplicationContext applicationContext) {
        this.serviceWorkerMap = new HashMap<>(256, 1);
        this.injecter = injecter;
        this.applicationContext = applicationContext;
        this.interceptorList = interceptorContext.getInterceptorList();
        this.nextIndexHandler = new LongRequestParameterHandlerImpl("nextIndex", Long.MAX_VALUE, 0);
        this.nextSizeHandler = new LongRequestParameterHandlerImpl("nextSize", 100, 1);
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
    public RequestParameterHandler getNextIndexHandler() {
        return this.nextIndexHandler;
    }

    @Override
    public RequestParameterHandler getNextSizeHandler() {
        return this.nextSizeHandler;
    }

    @Override
    public List<Interceptor> getInterceptorList() {
        return this.interceptorList;
    }
}
