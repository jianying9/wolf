package com.wolf.framework.worker;

import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.workhandler.WorkHandler;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public final class ServiceWorkerImpl implements ServiceWorker {

    private final WorkHandler nextWorkHandler;
    
    public ServiceWorkerImpl(final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
    }

    @Override
    public void doWork(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
    }
}
