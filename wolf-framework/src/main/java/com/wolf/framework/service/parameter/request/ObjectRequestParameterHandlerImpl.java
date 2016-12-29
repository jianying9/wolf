package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import java.util.List;
import java.util.Map;

/**
 * object类型处理类
 *
 * @author jianying9
 */
public final class ObjectRequestParameterHandlerImpl implements RequestParameterHandler {

    private final String name;
    private final String errorInfo = " must be object";
    private final boolean ignoreEmpty;
    private final String[] requiredParameter;
    private final String[] unrequiredParameter;
    private final Map<String, RequestParameterHandler> requestParameterHandlerMap;

    public ObjectRequestParameterHandlerImpl(
            final String name,
            boolean ignoreEmpty,
            String[] requiredParameter,
            String[] unrequiredParameter,
            Map<String, RequestParameterHandler> requestParameterHandlerMap
    ) {
        this.name = name;
        this.ignoreEmpty = ignoreEmpty;
        this.requestParameterHandlerMap = requestParameterHandlerMap;
        this.requiredParameter = requiredParameter;
        this.unrequiredParameter = unrequiredParameter;
    }

    private String validateRequiredParameter(Map<String, Object> parameterMap) {
        Object paraValue;
        String errorParaName = "";
        String errorMsg = "";
        String stringValue;
        List listValue;
        RequestParameterHandler parameterHandler;
        for (String parameter : this.requiredParameter) {
            paraValue = parameterMap.get(parameter);
            if (paraValue == null) {
                errorMsg = " is null";
                errorParaName = parameter;
                break;
            }
            if (String.class.isInstance(paraValue)) {
                stringValue = (String) paraValue;
                if (stringValue.isEmpty()) {
                    errorMsg = " is empty";
                    errorParaName = parameter;
                    break;
                }
            }
            if (List.class.isInstance(paraValue)) {
                listValue = (List) paraValue;
                if (listValue.isEmpty()) {
                    errorMsg = " is empty";
                    errorParaName = parameter;
                    break;
                }
            }
            parameterHandler = this.requestParameterHandlerMap.get(parameter);
            errorMsg = parameterHandler.validate(paraValue);
            if (errorMsg.isEmpty() == false) {
                errorParaName = parameter;
                break;
            }
        }
        if (errorMsg.isEmpty() == false) {
            errorMsg = this.name + "_" + errorParaName + errorMsg;
        }
        return errorMsg;
    }

    private String validateUnrequiredParameter(Map<String, Object> parameterMap) {
        Object paraValue;
        String stringValue;
        List listValue;
        String errorParaName = "";
        String errorMsg = "";
        RequestParameterHandler parameterHandler;
        for (String parameter : this.unrequiredParameter) {
            paraValue = parameterMap.get(parameter);
            if (paraValue != null) {
                parameterHandler = this.requestParameterHandlerMap.get(parameter);
                if (String.class.isInstance(paraValue)) {
                    stringValue = (String) paraValue;
                    if (stringValue.isEmpty() == false || parameterHandler.getIgnoreEmpty() == false) {
                        //如果输入参数不为空字符或则该参数不允许忽略空字符串,那么必须进行类型验证
                        errorMsg = parameterHandler.validate(paraValue);
                        if (errorMsg.isEmpty() == false) {
                            errorParaName = parameter;
                            break;
                        }
                    }
                } else if (List.class.isInstance(paraValue)) {
                    listValue = (List) paraValue;
                    if (listValue.isEmpty() == false || parameterHandler.getIgnoreEmpty() == false) {
                        //如果输入参数不为空字符或则该参数不允许忽略空字符串,那么必须进行类型验证
                        errorMsg = parameterHandler.validate(paraValue);
                        if (errorMsg.isEmpty() == false) {
                            errorParaName = parameter;
                            break;
                        }
                    }
                } else {
                    errorMsg = parameterHandler.validate(paraValue);
                    if (errorMsg.isEmpty() == false) {
                        errorParaName = parameter;
                        break;
                    }
                }
            }
        }
        if (errorMsg.isEmpty() == false) {
            errorMsg = this.name + "_" + errorParaName + errorMsg;
        }
        return errorMsg;
    }

    @Override
    public String validate(final Object value) {
        String msg = this.errorInfo;
        if (Map.class.isInstance(value)) {
            Map<String, Object> valueMap = (Map<String, Object>) value;
            //验证必填参数
            msg = this.validateRequiredParameter(valueMap);
            if (msg.isEmpty()) {
                msg = this.validateUnrequiredParameter(valueMap);
            }
        }
        return msg;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RequestDataType getDataType() {
        return RequestDataType.OBJECT;
    }

    @Override
    public boolean getIgnoreEmpty() {
        return ignoreEmpty;
    }

}
