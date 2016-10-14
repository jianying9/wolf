package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.Service;
import com.wolf.framework.worker.context.WorkerContext;

/**
 * 默认处理类
 *
 * @author aladdin
 */
public class DefaultWorkHandlerImpl implements WorkHandler {

    private final Service service;

    public DefaultWorkHandlerImpl(final Service service) {
        this.service = service;
    }

    @Override
    public void execute(WorkerContext workerContext) {
        this.service.execute(workerContext.getWorkerRequest(), workerContext.getWorkerResponse());
    }
}
