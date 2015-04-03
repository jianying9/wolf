package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;

/**
 * 次要参数处理类,保留空字符
 *
 * @author aladdin
 */
public class MinorParameterWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final String[] minorParameter;
    private final Map<String, RequestParameterHandler> parameterHandlerMap;

    public MinorParameterWorkHandlerImpl(
            final String[] minorParameter,
            final Map<String, RequestParameterHandler> parameterHandlerMap,
            final WorkHandler workHandler) {
        this.minorParameter = minorParameter;
        this.parameterHandlerMap = parameterHandlerMap;
        this.nextWorkHandler = workHandler;

    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        String paraValue;
        String errorParaName = "";
        String errorMsg = "";
        RequestParameterHandler parameterHandler;
        WorkerContext workerContext = frameworkMessageContext.getWorkerContext();
        //验证必要参数是否合法
        final Map<String, String> parameterMap = workerContext.getParameterMap();
        for (String parameter : this.minorParameter) {
            paraValue = parameterMap.get(parameter);
            if (paraValue != null) {
                //非空验证
                parameterHandler = this.parameterHandlerMap.get(parameter);
                errorMsg = parameterHandler.validate(paraValue);
                if (errorMsg.isEmpty()) {
                    frameworkMessageContext.putParameter(parameter, paraValue);
                } else {
                    errorParaName = parameter;
                    break;
                }
            }
        }
        if (errorMsg.isEmpty() == false) {
            errorMsg = errorParaName.concat(errorMsg);
        }
        if (errorMsg.isEmpty()) {
            //验证通过
            this.nextWorkHandler.execute(frameworkMessageContext);
        } else {
            frameworkMessageContext.invalid();
            frameworkMessageContext.setError(errorMsg);
            frameworkMessageContext.createErrorMessage();
        }
    }
}
