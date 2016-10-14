package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.WorkerContext;

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
    public void execute(WorkerContext workerContext) {
        this.nextWorkHandler.execute(workerContext);
        workerContext.removeSession();
    }
}
