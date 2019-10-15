package com.wolf.framework.worker;

import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.worker.context.WorkerContext;
import com.wolf.framework.worker.workhandler.WorkHandler;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public class ServiceWorkerImpl implements ServiceWorker {

    private final WorkHandler nextWorkHandler;
    private final ServiceContext serviceContext;

    public ServiceWorkerImpl(WorkHandler nextWorkHandler, ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
        this.nextWorkHandler = nextWorkHandler;
    }

    @Override
    public void doWork(WorkerContext workerContext) {
        this.nextWorkHandler.execute(workerContext);
    }

    @Override
    public ServiceContext getServiceContext() {
        return this.serviceContext;
    }
}
