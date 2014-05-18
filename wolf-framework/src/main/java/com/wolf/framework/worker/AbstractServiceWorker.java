package com.wolf.framework.worker;

import com.wolf.framework.worker.context.Response;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
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
    private String act = "";
    private String group = "";
    private String desc = "";
    private String requestConfigs = "[]";
    private String responseConfigs = "[]";
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
        infoMap.put("actionName", this.act);
        infoMap.put("group", this.group);
        infoMap.put("desc", this.desc);
        infoMap.put("requestConfigs", this.requestConfigs);
        infoMap.put("responseConfigs", this.responseConfigs);
        return infoMap;
    }

    private String getRequestParameterJson(RequestConfig[] requestConfigs) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        jsonBuilder.append('[');
        for (RequestConfig requestConfig : requestConfigs) {
            jsonBuilder.append("{\"name\":\"").append(requestConfig.name())
                    .append("\",\"must\":\"").append(requestConfig.must())
                    .append("\",\"type\":\"").append(requestConfig.typeEnum().name())
                    .append("\",\"desc\":\"").append(requestConfig.desc())
                    .append("\"}").append(',');
        }
        if (jsonBuilder.length() > 1) {
            jsonBuilder.setLength(jsonBuilder.length() - 1);
        }
        jsonBuilder.append(']');
        return jsonBuilder.toString();
    }

    private String getResponseParameterJson(ResponseConfig[] responseConfigs) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        jsonBuilder.append('[');
        for (ResponseConfig parameterConfig : responseConfigs) {
            jsonBuilder.append("{\"name\":\"").append(parameterConfig.name())
                    .append("\",\"type\":\"").append(parameterConfig.typeEnum().name())
                    .append("\",\"desc\":\"").append(parameterConfig.desc())
                    .append("\",\"filter\":\"");
            if (parameterConfig.filterTypes().length > 0) {
                for (FilterTypeEnum filterTypeEnum : parameterConfig.filterTypes()) {
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
    public final void createInfo(String act,
            String group,
            String description,
            RequestConfig[] requestConfigs,
            ResponseConfig[] responseConfigs) {
        this.group = group;
        this.desc = description;
        this.act = act;
        //构造请求参数信息
        this.requestConfigs = this.getRequestParameterJson(requestConfigs);
        //构造返回参数信息
        this.responseConfigs = this.getResponseParameterJson(responseConfigs);
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
