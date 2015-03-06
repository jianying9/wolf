package com.wolf.framework.worker;

import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.context.PageMessageContextImpl;
import com.wolf.framework.worker.context.WorkerContext;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.Map;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public final class PageServiceWorkerImpl extends AbstractServiceWorker {
    
    private final RequestParameterHandler pageIndexHandler;
    private final RequestParameterHandler pageSizeHandler;

    public PageServiceWorkerImpl(RequestParameterHandler pageIndexHandler, RequestParameterHandler pageSizeHandler, String[] returnParameter, Map<String, ResponseParameterHandler> fieldHandlerMap, WorkHandler nextWorkHandler) {
        super(returnParameter, fieldHandlerMap, nextWorkHandler);
        this.pageIndexHandler = pageIndexHandler;
        this.pageSizeHandler = pageSizeHandler;
    }

    @Override
    protected FrameworkMessageContext createFrameworkMessageContext(WorkerContext workerContext, String[] returnParameter, Map<String, ResponseParameterHandler> fieldHandlerMap) {
        return new PageMessageContextImpl(workerContext, returnParameter, fieldHandlerMap, this.pageIndexHandler, this.pageSizeHandler);
    }
}
