package com.wolf.framework.worker.workhandler;

import com.wolf.framework.session.Session;
import com.wolf.framework.worker.context.FrameworkMessageContext;

/**
 * session处理类
 *
 * @author aladdin
 */
public class CloseWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;

    public CloseWorkHandlerImpl(WorkHandler nextWorkHandler) {
        this.nextWorkHandler = nextWorkHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        this.nextWorkHandler.execute(frameworkMessageContext);
        Session session = frameworkMessageContext.getSession();
        if (session == null) {
            frameworkMessageContext.close();
        }
    }
}
