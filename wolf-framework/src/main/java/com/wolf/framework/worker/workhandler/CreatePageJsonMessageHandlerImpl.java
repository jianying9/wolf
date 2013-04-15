package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.parameter.ParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import java.util.Map;

/**
 * 生成json消息
 * @author aladdin
 */
public class CreatePageJsonMessageHandlerImpl implements WorkHandler {

    private final String[] returnParameter;
    private final Map<String, ParameterHandler> fieldHandlerMap;
    private final WorkHandler nextWorkHandler;

    public CreatePageJsonMessageHandlerImpl(final String[] returnParameter, final Map<String, ParameterHandler> fieldHandlerMap, final WorkHandler nextWorkHandler) {
        this.returnParameter = returnParameter;
        this.fieldHandlerMap = fieldHandlerMap;
        this.nextWorkHandler = nextWorkHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
        frameworkMessageContext.createPageMessage(returnParameter, fieldHandlerMap);
    }
}
