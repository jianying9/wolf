package com.wolf.framework.worker.workhandler;

import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.worker.context.WorkerContext;

/**
 * session读取及验证处理类
 *
 * @author aladdin
 */
public class ValidateSessionWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;

    public ValidateSessionWorkHandlerImpl(final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
    }

    @Override
    public void execute(WorkerContext workerContext) {
        String sid = workerContext.getSessionId();
        if (sid == null) {
            //返回未登录提示，关闭连接
            WorkerResponse response = workerContext.getWorkerResponse();
            response.unlogin();
        } else {
            this.nextWorkHandler.execute(workerContext);
        }
    }
}
