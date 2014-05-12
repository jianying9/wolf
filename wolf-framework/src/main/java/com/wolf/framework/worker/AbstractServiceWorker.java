package com.wolf.framework.worker;

import com.wolf.framework.service.parameter.InputConfig;
import com.wolf.framework.service.parameter.OutputConfig;
import com.wolf.framework.service.parameter.OutputParameterHandler;
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
    protected final Map<String, OutputParameterHandler> fieldHandlerMap;
    private final WorkHandler nextWorkHandler;
    private String act = "";
    private String group = "";
    private String description = "";
    private String importantParameterInfo = "[]";
    private String minorParameterInfo = "[]";
    private String returnParameterInfo = "[]";

    public AbstractServiceWorker(String[] returnParameter, Map<String, OutputParameterHandler> fieldHandlerMap, WorkHandler nextWorkHandler) {
        this.returnParameter = returnParameter;
        this.fieldHandlerMap = fieldHandlerMap;
        this.nextWorkHandler = nextWorkHandler;
    }

    protected abstract FrameworkMessageContext createFrameworkMessageContext(WorkerContext workerContext, String[] returnParameter, Map<String, OutputParameterHandler> fieldHandlerMap);

    @Override
    public void doWork(WorkerContext workerContext) {
        FrameworkMessageContext messageContext = this.createFrameworkMessageContext(workerContext, this.returnParameter, this.fieldHandlerMap);
        this.nextWorkHandler.execute(messageContext);
    }

    @Override
    public Map<String, String> getInfoMap() {
        Map<String, String> infoMap = new HashMap<String, String>(8, 1);
        infoMap.put("actionName", this.act);
        infoMap.put("group", this.group);
        infoMap.put("description", this.description);
        infoMap.put("importantParameter", this.importantParameterInfo);
        infoMap.put("minorParameter", this.minorParameterInfo);
        infoMap.put("returnParameter", this.returnParameterInfo);
        return infoMap;
    }

    private String getInputParameterJson(InputConfig[] parameters) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        jsonBuilder.append('[');
        for (InputConfig parameterConfig : parameters) {
            jsonBuilder.append("{\"name\":\"").append(parameterConfig.name())
                    .append("\",\"type\":\"").append(parameterConfig.typeEnum().name())
                    .append("\",\"description\":\"").append(parameterConfig.desc())
                    .append("\"}").append(',');
        }
        if (jsonBuilder.length() > 1) {
            jsonBuilder.setLength(jsonBuilder.length() - 1);
        }
        jsonBuilder.append(']');
        return jsonBuilder.toString();
    }

    private String getOutputParameterJson(OutputConfig[] parameters) {
        StringBuilder jsonBuilder = new StringBuilder(64);
        jsonBuilder.append('[');
        for (OutputConfig parameterConfig : parameters) {
            jsonBuilder.append("{\"name\":\"").append(parameterConfig.name())
                    .append("\",\"type\":\"").append(parameterConfig.typeEnum().name())
                    .append("\",\"description\":\"").append(parameterConfig.desc())
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
            InputConfig[] importantParameter,
            InputConfig[] minorParameter,
            OutputConfig[] returnParameter) {
        this.group = group;
        this.description = description;
        this.act = act;
        //构造重要参数信息
        this.importantParameterInfo = this.getInputParameterJson(importantParameter);
        //构造次要参数信息
        this.minorParameterInfo = this.getInputParameterJson(minorParameter);
        //构造返回参数信息
        this.returnParameterInfo = this.getOutputParameterJson(returnParameter);
    }

    @Override
    public final String getGroup() {
        return this.group;
    }

    @Override
    public final String getDescription() {
        return this.description;
    }
}
