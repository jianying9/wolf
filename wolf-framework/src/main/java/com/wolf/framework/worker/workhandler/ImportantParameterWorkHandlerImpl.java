package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.parameter.ParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import java.util.Map;

/**
 * 必要参数处理
 *
 * @author aladdin
 */
public class ImportantParameterWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final String[] importantParameter;
    private final Map<String, ParameterHandler> parameterHandlerMap;

    public ImportantParameterWorkHandlerImpl(
            final String[] importantParameter,
            final Map<String, ParameterHandler> parameterHandlerMap,
            final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
        this.importantParameter = importantParameter;
        this.parameterHandlerMap = parameterHandlerMap;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        String paraValue;
        String errorParaName = "";
        String errorMsg = "";
        ParameterHandler parameterHandler;
        Map<String, String> parameterMap = frameworkMessageContext.getParameterMap();
        //验证必要参数是否合法
        for (String parameter : this.importantParameter) {
            paraValue = parameterMap.get(parameter);
            if (paraValue == null) {
                errorMsg = WorkHandler.NULL_MESSAGE;
                errorParaName = parameter;
                break;
            }
            if (paraValue.isEmpty()) {
                errorMsg = WorkHandler.EMPTY_MESSAGE;
                errorParaName = parameter;
                break;
            }
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
        if (errorMsg.isEmpty()) {
            //验证通过
            this.nextWorkHandler.execute(frameworkMessageContext);
        } else {
            //返回错误消息
            errorMsg = errorParaName.concat(errorMsg);
            frameworkMessageContext.invalid();
            frameworkMessageContext.setError(errorMsg);
            frameworkMessageContext.createErrorMessage();
            frameworkMessageContext.sendMessage();
        }
    }
}
