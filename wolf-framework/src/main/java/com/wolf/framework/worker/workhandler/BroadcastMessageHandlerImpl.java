package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.FrameworkMessageContext;

/**
 *
 * @author aladdin
 */
public class BroadcastMessageHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;

    public BroadcastMessageHandlerImpl(WorkHandler nextWorkHandler) {
        this.nextWorkHandler = nextWorkHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
        frameworkMessageContext.broadcastMessage();
    }
}
