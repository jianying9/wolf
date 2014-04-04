package com.wolf.framework.worker;

import com.wolf.framework.service.parameter.ParameterConfig;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.workhandler.WorkHandler;

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

    private String getParameterJson(ParameterConfig[] parameters) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        for (ParameterConfig parameterConfig : parameters) {
            jsonBuilder.append("{\"name\":\"").append(parameterConfig)
                    .append("\",\"type\":\"").append(parameterConfig.basicTypeEnum().name())
                    .append("\",\"defaultValue\":\"").append(parameterConfig.defaultValue())
                    .append("\",\"description\":\"").append(parameterConfig.desc())
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
            ParameterConfig[] importantParameter,
            ParameterConfig[] minorParameter,
            ParameterConfig[] returnParameter) {
        this.group = group;
        this.description = description;
        //构造重要参数信息
        String importantData = this.getParameterJson(importantParameter);
        //构造次要参数信息
        String minorData = this.getParameterJson(minorParameter);
        //构造返回参数信息
        String returnData = this.getParameterJson(returnParameter);
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
