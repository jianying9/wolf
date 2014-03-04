package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.parameter.ParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import java.util.Map;

/**
 * 次要参数处理类,次要参数如果为null或则空字符串，用default值代替
 *
 * @author aladdin
 */
public class MinorParameterDefaultReplaceNullAndEmptyHandlerImpl implements MinorParameterHandler {

    private final String[] minorParameter;
    private final Map<String, ParameterHandler> parameterHandlerMap;

    public MinorParameterDefaultReplaceNullAndEmptyHandlerImpl(String[] minorParameter, Map<String, ParameterHandler> parameterHandlerMap) {
        this.minorParameter = minorParameter;
        this.parameterHandlerMap = parameterHandlerMap;
    }

    @Override
    public String execute(FrameworkMessageContext frameworkMessageContext) {
        String paraValue;
        String errorParaName = "";
        String errorMsg = "";
        ParameterHandler parameterHandler;
        Map<String, String> parameterMap = frameworkMessageContext.getParameterMap();
        //验证必要参数是否合法
        for (String parameter : this.minorParameter) {
            paraValue = parameterMap.get(parameter);
            parameterHandler = this.parameterHandlerMap.get(parameter);
            if (paraValue == null) {
                paraValue = parameterHandler.getDefaultValue();
                frameworkMessageContext.putParameter(parameter, paraValue);
            } else {
                if (paraValue.isEmpty()) {
                    paraValue = parameterHandler.getDefaultValue();
                    frameworkMessageContext.putParameter(parameter, paraValue);
                } else {
                    errorMsg = parameterHandler.validate(paraValue);
                    if (errorMsg.isEmpty()) {
                        paraValue = parameterHandler.convertToInput(paraValue);
                        frameworkMessageContext.putParameter(parameter, paraValue);
                    } else {
                        errorParaName = parameter;
                        break;
                    }
                }
            }
        }
        if (errorMsg.isEmpty() == false) {
            errorMsg = errorParaName.concat(errorMsg);
        }
        return errorMsg;
    }
}
