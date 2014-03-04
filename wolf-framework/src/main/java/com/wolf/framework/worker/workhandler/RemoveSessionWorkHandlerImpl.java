package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.FrameworkMessageContext;

/**
 * 移除session
 *
 * @author aladdin
 */
public class RemoveSessionWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;

    public RemoveSessionWorkHandlerImpl(final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
        frameworkMessageContext.removeSession();
    }
}
