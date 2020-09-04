package com.wolf.framework.worker.workhandler;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.exception.ResponseCodeException;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.Service;
import com.wolf.framework.worker.context.WorkerContext;
import org.apache.logging.log4j.Logger;

/**
 * 默认处理类
 *
 * @author aladdin
 */
public class DefaultServiceWorkHandlerImpl implements WorkHandler {

    private final Service service;

    public DefaultServiceWorkHandlerImpl(final Service service) {
        this.service = service;
    }

    @Override
    public void execute(WorkerContext workerContext) {
        try {
            this.service.execute(workerContext.getWorkerRequest(), workerContext.getWorkerResponse());
        } catch (RuntimeException re) {
            Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
            logger.error("wolf-exception", re);
            Throwable t = re.getCause();
            if (t == null) {
                t = re;
            }
            Response response = workerContext.getWorkerResponse();
            if (ResponseCodeException.class.isInstance(t)) {
                ResponseCodeException te = (ResponseCodeException) t;
                response.setCode(te.getCode(), te.getDesc());
            } else if (UnsupportedOperationException.class.isInstance(t)) {
                response.unsupport();
            } else {
                response.exception();
            }
        }
    }
}
