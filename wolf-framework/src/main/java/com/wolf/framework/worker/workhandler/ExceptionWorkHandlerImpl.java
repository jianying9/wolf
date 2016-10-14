package com.wolf.framework.worker.workhandler;

import com.wolf.framework.config.ResponseState;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.exception.ResponseStateException;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.worker.context.WorkerContext;
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
    public void execute(WorkerContext workerContext) {
        try {
            this.nextWorkHandler.execute(workerContext);
        } catch (RuntimeException re) {
            Throwable t = re.getCause();
            if (t == null) {
                t = re;
            }
            Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
            logger.error("wolf-exception", t);
            Response response = workerContext.getWorkerResponse();
            if (ResponseStateException.class.isInstance(t)) {
                ResponseStateException te = (ResponseStateException) t;
                response.setState(te.getState());
            } else if(UnsupportedOperationException.class.isInstance(t)) {
                response.setState(ResponseState.UNSUPPORT);
            } else {
                response.setState(ResponseState.EXCEPTION);
            }
        }
    }
}
