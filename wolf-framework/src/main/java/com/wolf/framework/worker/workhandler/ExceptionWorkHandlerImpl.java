package com.wolf.framework.worker.workhandler;

import com.wolf.framework.config.DefaultResponseStates;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.exception.ResponseStateException;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import org.slf4j.Logger;

/**
 * 事物处理类
 *
 * @author aladdin
 */
public class ExceptionWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;

    public ExceptionWorkHandlerImpl(final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        try {
            this.nextWorkHandler.execute(frameworkMessageContext);
        } catch (RuntimeException re) {
            Throwable t = re.getCause();
            if (t == null) {
                t = re;
            }
            Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
            logger.error("wolf-exception", t);
            if (ResponseStateException.class.isInstance(t)) {
                ResponseStateException te = (ResponseStateException) t;
                frameworkMessageContext.setState(te.getState());
            } else {
                frameworkMessageContext.setState(DefaultResponseStates.EXCEPTION);
            }
        }
    }
}
