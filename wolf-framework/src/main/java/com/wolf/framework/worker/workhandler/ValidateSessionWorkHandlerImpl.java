package com.wolf.framework.worker.workhandler;

import com.wolf.framework.session.Session;
import com.wolf.framework.worker.context.FrameworkMessageContext;
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
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        Session session = frameworkMessageContext.getSession();
        if (session == null) {
            //返回未登录提示，关闭连接
            frameworkMessageContext.unlogin();
            String message = frameworkMessageContext.createErrorMessage();
            WorkerContext workerContext = frameworkMessageContext.getWorkerContext();
            workerContext.setResponseMessage(message);
            workerContext.sendMessage();
        } else {
            this.nextWorkHandler.execute(frameworkMessageContext);
        }
    }
}
