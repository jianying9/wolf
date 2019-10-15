package com.wolf.framework.worker.workhandler;

import com.wolf.framework.interceptor.Interceptor;
import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.WorkerRequest;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 拦截处理环节处理类
 *
 * @author aladdin
 */
public class InterceptorWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final List<Interceptor> interceptorBeforeList;
    private final List<Interceptor> interceptorAfterList;

    public InterceptorWorkHandlerImpl(final WorkHandler workHandler, List<Interceptor> interceptorList) {
        this.nextWorkHandler = workHandler;
        this.interceptorAfterList = new ArrayList(0);
        this.interceptorBeforeList = new ArrayList(0);
        for (Interceptor interceptor : interceptorList) {
            if (interceptor.isAfter()) {
                this.interceptorAfterList.add(interceptor);
            } else {
                this.interceptorBeforeList.add(interceptor);
            }
        }
    }

    @Override
    public void execute(WorkerContext workerContext) {
        WorkerRequest workerRequest = workerContext.getWorkerRequest();
        WorkerResponse workerResponse = workerContext.getWorkerResponse();
        boolean isContinue = true;
        //前置拦截器执行
        for (Interceptor interceptor : this.interceptorBeforeList) {
            isContinue = interceptor.execute(workerRequest, workerResponse);
            if (isContinue == false) {
                //中断拦截器
                break;
            }
        }
        if (isContinue) {
            //服务执行
            this.nextWorkHandler.execute(workerContext);
            //后置拦截器执行
            for (Interceptor interceptor : this.interceptorAfterList) {
                isContinue = interceptor.execute(workerRequest, workerResponse);
                if (isContinue == false) {
                    //中断拦截器
                    break;
                }
            }
        }
    }

}
