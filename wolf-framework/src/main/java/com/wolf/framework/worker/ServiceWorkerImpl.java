package com.wolf.framework.worker;

import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.filter.EscapeFilterImpl;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterType;
import com.wolf.framework.worker.context.WorkerContext;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.HashMap;
import java.util.Map;
import com.wolf.framework.service.ResponseCode;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public class ServiceWorkerImpl implements ServiceWorker {

    private final WorkHandler nextWorkHandler;
    private String requestConfigs = "[]";
    private String responseConfigs = "[]";
    private String responseCodes = "[]";
    private final ServiceContext serviceContext;
    
    public ServiceWorkerImpl(WorkHandler nextWorkHandler, ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
        this.nextWorkHandler = nextWorkHandler;
    }

    @Override
    public void doWork(WorkerContext workerContext) {
        this.nextWorkHandler.execute(workerContext);
    }

    @Override
    public Map<String, String> getInfoMap() {
        Map<String, String> infoMap = new HashMap<String, String>(8, 1);
        infoMap.put("route", this.serviceContext.route());
        infoMap.put("group", this.serviceContext.group());
        infoMap.put("page", Boolean.toString(this.serviceContext.page()));
        infoMap.put("validateSession", Boolean.toString(this.serviceContext.validateSession()));
        infoMap.put("desc", this.serviceContext.desc());
        infoMap.put("requestConfigs", this.requestConfigs);
        infoMap.put("responseConfigs", this.responseConfigs);
        infoMap.put("responseCodes", this.responseCodes);
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
                for (FilterType filterType : responseConfig.filterTypes()) {
                    jsonBuilder.append(filterType.name()).append(',');
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
    public final void createInfo() {
        Filter escapeFilter = new EscapeFilterImpl();
        //构造请求参数信息
        this.requestConfigs = this.getRequestParameterJson(this.serviceContext.requestConfigs(), escapeFilter);
        //构造返回参数信息
        this.responseConfigs = this.getResponseParameterJson(this.serviceContext.responseConfigs(), escapeFilter);
        //返回状态提示
        StringBuilder responseStateBuilder = new StringBuilder(64);
        responseStateBuilder.append('[');
        for (ResponseCode responseCode : this.serviceContext.responseCodes()) {
            responseStateBuilder.append("{\"code\":\"").append(responseCode.code())
                    .append("\",\"desc\":\"").append(escapeFilter.doFilter(responseCode.desc()))
                    .append("\"}").append(',');
        }
        if (responseStateBuilder.length() > 1) {
            responseStateBuilder.setLength(responseStateBuilder.length() - 1);
        }
        responseStateBuilder.append(']');
        this.responseCodes = responseStateBuilder.toString();
    }

    @Override
    public String getGroup() {
        return this.serviceContext.group();
    }

    @Override
    public String getRoute() {
        return this.serviceContext.route();
    }

    @Override
    public String getDesc() {
        return this.serviceContext.desc();
    }
}
