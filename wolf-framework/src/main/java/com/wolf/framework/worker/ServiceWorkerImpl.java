package com.wolf.framework.worker;

import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.context.MessageContextImpl;
import com.wolf.framework.worker.context.WorkerContext;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.Map;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public final class ServiceWorkerImpl extends AbstractServiceWorker {

    public ServiceWorkerImpl(String[] returnParameter, Map<String, ResponseParameterHandler> fieldHandlerMap, WorkHandler nextWorkHandler) {
        super(returnParameter, fieldHandlerMap, nextWorkHandler);
    }

    @Override
    protected FrameworkMessageContext createFrameworkMessageContext(WorkerContext workerContext, String[] returnParameter, Map<String, ResponseParameterHandler> fieldHandlerMap) {
        return new MessageContextImpl(workerContext, returnParameter, fieldHandlerMap);
    }
}
