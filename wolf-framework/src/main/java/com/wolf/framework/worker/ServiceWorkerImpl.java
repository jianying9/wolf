package com.wolf.framework.worker;

import com.wolf.framework.service.parameter.ParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.Map;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public final class ServiceWorkerImpl implements ServiceWorker {

    private final WorkHandler nextWorkHandler;
    private String info = "";
    private String group = "";
    private String description = "";

    public ServiceWorkerImpl(final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
    }

    @Override
    public void doWork(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
    }

    @Override
    public String getInfo() {
        return info;
    }

    private String getParameterJson(String[] parameters, Map<String, ParameterHandler> parameterHandlerMap) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        ParameterHandler parameterHandler;
        for (String parameter : parameters) {
            parameterHandler = parameterHandlerMap.get(parameter);
            jsonBuilder.append("{\"name\":\"").append(parameter)
                    .append("\",\"type\":\"").append(parameterHandler.getDataType())
                    .append("\",\"defaultValue\":\"").append(parameterHandler.getDefaultValue())
                    .append("\",\"description\":\"").append(parameterHandler.getDescription())
                    .append("\"}").append(',');
        }
        if (jsonBuilder.length() > 0) {
            jsonBuilder.setLength(jsonBuilder.length() - 1);
        }
        return jsonBuilder.toString();
    }

    public void createInfo(String act,
            String group,
            String description,
            String[] importantParameter,
            String[] minorParameter,
            String[] returnParameter,
            Map<String, ParameterHandler> parameterHandlerMap) {
        this.group = group;
        this.description = description;
        //构造重要参数信息
        String importantData = this.getParameterJson(importantParameter, parameterHandlerMap);
        //构造次要参数信息
        String minorData = this.getParameterJson(minorParameter, parameterHandlerMap);
        //构造返回参数信息
        String returnData = this.getParameterJson(returnParameter, parameterHandlerMap);
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"actionName\":\"").append(act);
        jsonBuilder.append("\",\"group\":\"").append(group);
        jsonBuilder.append("\",\"description\":\"").append(description);
        jsonBuilder.append("\",\"importantParameter\":[").append(importantData);
        jsonBuilder.append("],\"minorParameter\":[").append(minorData);
        jsonBuilder.append("],\"returnParameter\":[").append(returnData);
        jsonBuilder.append("]}");
        this.info = jsonBuilder.toString();
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
