package com.wolf.framework.worker;

import com.wolf.framework.service.parameter.InputParameterHandler;
import com.wolf.framework.service.parameter.OutputParameterHandler;
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
    
    private final InputParameterHandler pageIndexHandler;
    private final InputParameterHandler pageSizeHandler;

    public PageServiceWorkerImpl(InputParameterHandler pageIndexHandler, InputParameterHandler pageSizeHandler, String[] returnParameter, Map<String, OutputParameterHandler> fieldHandlerMap, WorkHandler nextWorkHandler) {
        super(returnParameter, fieldHandlerMap, nextWorkHandler);
        this.pageIndexHandler = pageIndexHandler;
        this.pageSizeHandler = pageSizeHandler;
    }

    @Override
    protected FrameworkMessageContext createFrameworkMessageContext(WorkerContext workerContext, String[] returnParameter, Map<String, OutputParameterHandler> fieldHandlerMap) {
        return new PageMessageContextImpl(workerContext, returnParameter, fieldHandlerMap, this.pageIndexHandler, this.pageSizeHandler);
    }
}
