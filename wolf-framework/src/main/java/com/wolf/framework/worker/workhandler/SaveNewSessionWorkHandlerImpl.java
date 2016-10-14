package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.WorkerContext;

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
    public void execute(WorkerContext workerContext) {
        this.nextWorkHandler.execute(workerContext);
        String newSid = workerContext.getWorkerRequest().getNewSessionId();
        workerContext.saveNewSession(newSid);
    }
}
