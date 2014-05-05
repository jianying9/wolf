package com.wolf.framework.timer;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.LocalWorkerContextImpl;
import java.util.Map;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public class AbstractTimer {

    protected String executeService(final String act, final Map<String, String> parameterMap) {
        String result = "";
        if (ApplicationContext.CONTEXT.isReady()) {
            ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(act);
            if (serviceWorker == null) {
                Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
                logger.error("timer:Can not find act:".concat(act));
            } else {
                LocalWorkerContextImpl localWorkerContextImpl = new LocalWorkerContextImpl(null, act, parameterMap);
                serviceWorker.doWork(localWorkerContextImpl);
                result = localWorkerContextImpl.getResponseMessage();
            }
        } else {
            Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
            logger.warn("timer:System is not ready! Wait for next time.");
        }
        return result;
    }
}
