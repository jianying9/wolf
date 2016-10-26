package com.wolf.framework.worker.workhandler;

import com.wolf.framework.interceptor.Interceptor;
import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.WorkerRequest;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.List;

/**
 * 拦截处理环节处理类
 *
 * @author aladdin
 */
public class InterceptorWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final List<Interceptor> interceptorList;

    public InterceptorWorkHandlerImpl(final WorkHandler workHandler, List<Interceptor> interceptorList) {
        this.nextWorkHandler = workHandler;
        this.interceptorList = interceptorList;
    }

    @Override
    public void execute(WorkerContext workerContext) {
        WorkerRequest workerRequest = workerContext.getWorkerRequest();
        WorkerResponse workerResponse = workerContext.getWorkerResponse();
        boolean isContinue = true;
        for (Interceptor interceptor : this.interceptorList) {
            isContinue = interceptor.execute(workerRequest, workerResponse);
            if(isContinue == false) {
                //中断拦截器
                break;
            }
        }
        if(isContinue) {
            //服务执行
            this.nextWorkHandler.execute(workerContext);
        }
    }
}
