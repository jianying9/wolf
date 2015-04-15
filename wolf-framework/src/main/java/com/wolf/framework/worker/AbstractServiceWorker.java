package com.wolf.framework.worker;

import com.wolf.framework.service.ResponseState;
import com.wolf.framework.worker.context.Response;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.parameter.filter.EscapeFilterImpl;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterTypeEnum;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.context.WorkerContext;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public abstract class AbstractServiceWorker implements ServiceWorker {

    protected final String[] returnParameter;
    protected final Map<String, ResponseParameterHandler> fieldHandlerMap;
    private final WorkHandler nextWorkHandler;
    private String route = "";
    private String group = "";
    private boolean page;
    private boolean validateSession;
    private String desc = "";
    private String requestConfigs = "[]";
    private String responseConfigs = "[]";
    private String responseStates = "[]";
    protected FrameworkMessageContext frameworkMessageContext;

    public AbstractServiceWorker(String[] returnParameter, Map<String, ResponseParameterHandler> fieldHandlerMap, WorkHandler nextWorkHandler) {
        this.returnParameter = returnParameter;
        this.fieldHandlerMap = fieldHandlerMap;
        this.nextWorkHandler = nextWorkHandler;
    }

    protected abstract FrameworkMessageContext createFrameworkMessageContext(WorkerContext workerContext, String[] returnParameter, Map<String, ResponseParameterHandler> fieldHandlerMap);

    @Override
    public void doWork(WorkerContext workerContext) {
        this.frameworkMessageContext = this.createFrameworkMessageContext(workerContext, this.returnParameter, this.fieldHandlerMap);
        this.nextWorkHandler.execute(this.frameworkMessageContext);
    }

    @Override
    public Map<String, String> getInfoMap() {
        Map<String, String> infoMap = new HashMap<String, String>(8, 1);
        infoMap.put("route", this.route);
        infoMap.put("group", this.group);
        infoMap.put("page", Boolean.toString(this.page));
        infoMap.put("validateSession", Boolean.toString(this.validateSession));
        infoMap.put("desc", this.desc);
        infoMap.put("requestConfigs", this.requestConfigs);
        infoMap.put("responseConfigs", this.responseConfigs);
        infoMap.put("responseStates", this.responseStates);
        return infoMap;
    }

    private String getRequestParameterJson(RequestConfig[] requestConfigs, Filter escapeFilter) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        jsonBuilder.append('[');
        for (RequestConfig requestConfig : requestConfigs) {
            jsonBuilder.append("{\"name\":\"").append(requestConfig.name())
                    .append("\",\"must\":\"").append(requestConfig.must())
                    .append("\",\"type\":\"").append(requestConfig.dataType().name())
                    .append("\",\"desc\":\"").append(escapeFilter.doFilter(requestConfig.desc()))
                    .append("\"}").append(',');
        }
        if (jsonBuilder.length() > 1) {
            jsonBuilder.setLength(jsonBuilder.length() - 1);
        }
        jsonBuilder.append(']');
        return jsonBuilder.toString();
    }

    private String getResponseParameterJson(ResponseConfig[] responseConfigs, Filter escapeFilter) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        jsonBuilder.append('[');
        for (ResponseConfig responseConfig : responseConfigs) {
            jsonBuilder.append("{\"name\":\"").append(responseConfig.name())
                    .append("\",\"type\":\"").append(responseConfig.dataType().name())
                    .append("\",\"desc\":\"").append(escapeFilter.doFilter(responseConfig.desc()))
                    .append("\",\"filter\":\"");
            if (responseConfig.filterTypes().length > 0) {
                for (FilterTypeEnum filterTypeEnum : responseConfig.filterTypes()) {
                    jsonBuilder.append(filterTypeEnum.name()).append(',');
                }
                jsonBuilder.setLength(jsonBuilder.length() - 1);
            }
            jsonBuilder.append("\"}").append(',');
        }
        if (jsonBuilder.length() > 1) {
            jsonBuilder.setLength(jsonBuilder.length() - 1);
        }
        jsonBuilder.append(']');
        return jsonBuilder.toString();
    }

    @Override
    public final void createInfo(String route,
            boolean page,
            boolean validateSession,
            String group,
            String description,
            RequestConfig[] requestConfigs,
            ResponseConfig[] responseConfigs,
            ResponseState[] responseStates) {
        Filter escapeFilter = new EscapeFilterImpl();
        this.group = group;
        this.page = page;
        this.validateSession = validateSession;
        this.desc = escapeFilter.doFilter(description);
        this.route = route;
        //构造请求参数信息
        this.requestConfigs = this.getRequestParameterJson(requestConfigs, escapeFilter);
        //构造返回参数信息
        this.responseConfigs = this.getResponseParameterJson(responseConfigs, escapeFilter);
        //返回状态提示
        StringBuilder responseStateBuilder = new StringBuilder(64);
        responseStateBuilder.append('[');
        for (ResponseState responseState : responseStates) {
            responseStateBuilder.append("{\"state\":\"").append(responseState.state())
                    .append("\",\"desc\":\"").append(escapeFilter.doFilter(responseState.desc()))
                    .append("\"}").append(',');
        }
        if (responseStateBuilder.length() > 1) {
            responseStateBuilder.setLength(responseStateBuilder.length() - 1);
        }
        responseStateBuilder.append(']');
        this.responseStates = responseStateBuilder.toString();
    }
    
    @Override
    public final String getRoute() {
        return this.route;
    }

    @Override
    public final String getGroup() {
        return this.group;
    }

    @Override
    public final String getDescription() {
        return this.desc;
    }

    @Override
    public final Response getResponse() {
        return this.frameworkMessageContext;
    }
}
