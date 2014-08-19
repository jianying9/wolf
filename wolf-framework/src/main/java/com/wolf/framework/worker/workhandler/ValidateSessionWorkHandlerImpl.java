package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.FrameworkMessageContext;

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
        String sid = frameworkMessageContext.getSessionId();
        if (sid == null) {
            //返回未登录提示，关闭连接
            frameworkMessageContext.unlogin();
            frameworkMessageContext.createErrorMessage();
        } else {
            this.nextWorkHandler.execute(frameworkMessageContext);
        }
    }
}
