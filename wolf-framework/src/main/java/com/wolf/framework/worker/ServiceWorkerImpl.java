package com.wolf.framework.worker;

import com.wolf.framework.config.ResponseCodeConfig;
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
import com.wolf.framework.service.parameter.RequestDataType;
import java.util.Set;

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
        Map<String, String> infoMap = new HashMap<>(8, 1);
        infoMap.put("route", this.serviceContext.route());
        infoMap.put("group", this.serviceContext.group());
        infoMap.put("page", Boolean.toString(this.serviceContext.page()));
        infoMap.put("validateSession", Boolean.toString(this.serviceContext.validateSession()));
        infoMap.put("desc", this.serviceContext.desc());
        infoMap.put("requestConfigs", this.requestConfigs);
        infoMap.put("responseConfigs", this.responseConfigs);
        infoMap.put("responseCodes", this.responseCodes);
        infoMap.put("hasAsyncResponse", Boolean.toString(this.serviceContext.hasAsyncResponse()));
        return infoMap;
    }

    private String getRequestParameterJson(RequestConfig[] requestConfigs, Filter escapeFilter) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        RequestDataType type;
        String typeStr;
        jsonBuilder.append('[');
        boolean ignoreEmpty;
        for (RequestConfig requestConfig : requestConfigs) {
            type = requestConfig.dataType();
            typeStr = type.name();
            if (type == RequestDataType.LONG || type == RequestDataType.DOUBLE || type == RequestDataType.STRING) {
                typeStr = typeStr + "[" + requestConfig.min() + "," + requestConfig.max() + "]";
            }
            ignoreEmpty = requestConfig.ignoreEmpty();
            if (requestConfig.required()) {
                ignoreEmpty = false;
            }
            jsonBuilder.append("{\"name\":\"").append(requestConfig.name())
                    .append("\",\"required\":").append(requestConfig.required())
                    .append(",\"ignoreEmpty\":").append(ignoreEmpty)
                    .append(",\"type\":\"").append(typeStr)
                    .append("\",\"desc\":\"").append(escapeFilter.doFilter(requestConfig.desc()))
                    .append("\"}").append(',');
        }
        if (this.serviceContext.page()) {
            jsonBuilder.append("{\"name\":\"").append("nextIndex")
                    .append("\",\"required\":").append("false")
                    .append(",\"ignoreEmpty\":").append("true")
                    .append(",\"type\":\"").append("LONG")
                    .append("\",\"desc\":\"").append("分页起始记录id")
                    .append("\"}").append(',');
            jsonBuilder.append("{\"name\":\"").append("nextSize")
                    .append("\",\"required\":").append("false")
                    .append(",\"ignoreEmpty\":").append("true")
                    .append(",\"type\":\"").append("LONG[1,100]")
                    .append("\",\"desc\":\"").append("分页读取记录数量")
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
        Map<String, String> responseCodeMap = new HashMap<>();
        responseCodeMap.put(ResponseCodeConfig.SUCCESS, "操作成功");
        responseCodeMap.put(ResponseCodeConfig.INVALID, "非法参数");
        responseCodeMap.put(ResponseCodeConfig.UNLOGIN, "未登陆");
        responseCodeMap.put(ResponseCodeConfig.TIMEOUT, "session超期");
        responseCodeMap.put(ResponseCodeConfig.DENIED, "无权限访问");
        responseCodeMap.put(ResponseCodeConfig.NOTFOUND, "服务部存在");
        responseCodeMap.put(ResponseCodeConfig.SUCCESS, "操作成功");
        //构造请求参数信息
        this.requestConfigs = this.getRequestParameterJson(this.serviceContext.requestConfigs(), escapeFilter);
        //构造返回参数信息
        this.responseConfigs = this.getResponseParameterJson(this.serviceContext.responseConfigs(), escapeFilter);
        //返回状态提示
        StringBuilder responseCodeBuilder = new StringBuilder(64);
        responseCodeBuilder.append('[');
        for (ResponseCode responseCode : this.serviceContext.responseCodes()) {
            responseCodeMap.remove(responseCode.code());
            responseCodeBuilder.append("{\"code\":\"").append(responseCode.code())
                    .append("\",\"desc\":\"").append(escapeFilter.doFilter(responseCode.desc()))
                    .append("\",\"asycn\":").append(Boolean.toString(responseCode.async()))
                    .append(",\"type\":\"custom\"}").append(',');
        }
        Set<Map.Entry<String, String>> codeSet = responseCodeMap.entrySet();
        for (Map.Entry<String, String> entry : codeSet) {
            responseCodeBuilder.append("{\"code\":\"").append(entry.getKey())
                    .append("\",\"desc\":\"").append(escapeFilter.doFilter(entry.getValue()))
                    .append("\",\"asycn\":").append("false")
                    .append(",\"type\":\"global\"}").append(',');
        }
        if (responseCodeBuilder.length() > 1) {
            responseCodeBuilder.setLength(responseCodeBuilder.length() - 1);
        }
        responseCodeBuilder.append(']');
        this.responseCodes = responseCodeBuilder.toString();
    }

    @Override
    public ServiceContext getServiceContext() {
        return this.serviceContext;
    }
}
