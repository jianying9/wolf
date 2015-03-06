package com.wolf.framework.test;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.ApplicationContextBuilder;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.utils.SecurityUtils;
import com.wolf.framework.worker.context.Response;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.LocalWorkerContextImpl;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public final class TestHandler {

    private String sid;

    public TestHandler(Map<String, String> parameterMap) {
        synchronized (TestHandler.class) {
            if (ApplicationContext.CONTEXT.isReady() == false) {
                parameterMap.put(FrameworkConfig.COMPILE_MODEL, FrameworkConfig.UNIT_TEST);
                ApplicationContextBuilder applicationContextBuilder = new ApplicationContextBuilder(parameterMap);
                applicationContextBuilder.build();
            }
        }
    }

    public void setSessionId(String sid) {
        this.sid = sid;
    }

    public Response execute(String act, Map<String, String> parameterMap) {
        Response result;
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(act);
        if (serviceWorker == null) {
            Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
            logger.error("timer:Can not find act:".concat(act));
            result = null;
        } else {
            String key = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.SEED_DES_KEY);
            String seed = Long.toString(System.currentTimeMillis());
            byte[] entrySeedByte = SecurityUtils.encryptByDes(seed, key);
            String engrySeedHex = SecurityUtils.byteToHexString(entrySeedByte);
            parameterMap.put("seed", engrySeedHex);
            WorkerContext workerContext = new LocalWorkerContextImpl(this.sid, act, parameterMap);
            serviceWorker.doWork(workerContext);
            result = serviceWorker.getResponse();
        }
        return result;
    }
}
