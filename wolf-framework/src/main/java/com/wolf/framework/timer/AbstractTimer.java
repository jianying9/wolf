package com.wolf.framework.timer;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.LocalMessageContextImpl;
import java.util.Map;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public class AbstractTimer {

    protected void executeService(final String act, final Map<String, String> parameterMap) {
        if (ApplicationContext.CONTEXT.isReady()) {
            ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(act);
            if (serviceWorker == null) {
                Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
                logger.error("timer:Can not find act:".concat(act));
            } else {
                LocalMessageContextImpl localMessageContextImpl = new LocalMessageContextImpl(null, act, parameterMap);
                serviceWorker.doWork(localMessageContextImpl);
            }
        } else {
            Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
            logger.warn("timer:System is not ready! Wait for next time.");
        }
    }
}
