package com.wolf.framework.worker.workhandler;

import com.wolf.framework.session.Session;
import com.wolf.framework.worker.context.FrameworkMessageContext;
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
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
        Session newSession = frameworkMessageContext.getNewSession();
        WorkerContext workerContext = frameworkMessageContext.getWorkerContext();
        workerContext.saveNewSession(newSession);
    }
}
