package com.wolf.framework.request;

import com.wolf.framework.worker.context.WorkerContext;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class RequestImpl implements WorkerRequest {

    private final WorkerContext workerContext;
    private final Map<String, Object> parameterMap;

    public RequestImpl(WorkerContext workerContext) {
        this.workerContext = workerContext;
        this.parameterMap = new HashMap<>(8, 1);
    }

    @Override
    public Map<String, Object> getParameterMap() {
        return this.parameterMap;
    }

    @Override
    public String getRoute() {
        return this.workerContext.getRoute();
    }

    @Override
    public String getSessionId() {
        return this.workerContext.getSessionId();
    }

    @Override
    public void removeSession() {
        this.workerContext.removeSession();
    }

    @Override
    public Object getParameter(String name) {
        return this.parameterMap.get(name);
    }

    @Override
    public void putParameter(String name, Object value) {
        this.parameterMap.put(name, value);
    }

}
