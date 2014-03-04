package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.FrameworkMessageContext;

/**
 * 保存新的session
 *
 * @author aladdin
 */
public class SaveNewSessionWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;

    public SaveNewSessionWorkHandlerImpl(final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
        frameworkMessageContext.saveNewSession();
    }
}
