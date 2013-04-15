package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.parameter.ParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import java.util.Map;

/**
 * 生成json消息
 * @author aladdin
 */
public class CreateJsonMessageHandlerImpl implements WorkHandler {

    private final String[] returnParameter;
    private final Map<String, ParameterHandler> parameterHandlerMap;
    private final WorkHandler nextWorkHandler;

    public CreateJsonMessageHandlerImpl(final String[] returnParameter, final Map<String, ParameterHandler> parameterHandlerMap, final WorkHandler nextWorkHandler) {
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = parameterHandlerMap;
        this.nextWorkHandler = nextWorkHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
        frameworkMessageContext.createMessage(returnParameter, parameterHandlerMap);
    }
}
