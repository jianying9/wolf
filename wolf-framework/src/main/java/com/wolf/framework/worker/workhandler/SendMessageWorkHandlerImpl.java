package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.FrameworkMessageContext;

/**
 * JSON输出处理类
 *
 * @author aladdin
 */
public class SendMessageWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;

    public SendMessageWorkHandlerImpl(final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
        frameworkMessageContext.sendMessage();
    }
}
