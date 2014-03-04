package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.parameter.ParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import java.util.Map;

/**
 * 次要参数处理类,丢弃空字符
 *
 * @author aladdin
 */
public class MinorParameterDiscardEmptyHandlerImpl implements MinorParameterHandler {

    private final String[] minorParameter;
    private final Map<String, ParameterHandler> parameterHandlerMap;

    public MinorParameterDiscardEmptyHandlerImpl(String[] minorParameter, Map<String, ParameterHandler> parameterHandlerMap) {
        this.minorParameter = minorParameter;
        this.parameterHandlerMap = parameterHandlerMap;
    }

    @Override
    public String execute(FrameworkMessageContext frameworkMessageContext) {
        String paraValue;
        String errorParaName = "";
        String errorMsg = "";
        ParameterHandler parameterHandler;
        //验证必要参数是否合法
        Map<String, String> parameterMap = frameworkMessageContext.getParameterMap();
        for (String parameter : this.minorParameter) {
            paraValue = parameterMap.get(parameter);
            if (paraValue != null) {
                if (paraValue.isEmpty()) {
                    //丢弃空值
                    frameworkMessageContext.removeParameter(parameter);
                } else {
                    //非空验证
                    parameterHandler = this.parameterHandlerMap.get(parameter);
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
        if (!errorMsg.isEmpty()) {
            errorMsg = errorParaName.concat(errorMsg);
        }
        return errorMsg;
    }
}
