package com.wolf.framework.test;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.ApplicationContextBuilder;
import com.wolf.framework.logger.AccessLogger;
import com.wolf.framework.logger.AccessLoggerFactory;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.LocalWorkerContextImpl;
import java.io.IOException;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
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

    public Response execute(String route, Map<String, Object> parameterMap) {
        Response result;
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
        if (serviceWorker == null) {
            Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
            logger.error("timer:Can not find route:".concat(route));
            result = null;
        } else {
            LocalWorkerContextImpl workerContext = new LocalWorkerContextImpl(this.sid, route, serviceWorker);
            workerContext.initLocalParameter(parameterMap);
            serviceWorker.doWork(workerContext);
            result = workerContext.getWorkerResponse();
            //
            ObjectMapper mapper = new ObjectMapper();
            String json = "";
            try {
                json = mapper.writeValueAsString(parameterMap);
            } catch (IOException ex) {
            }
            AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
            accessLogger.log(route, sid, json, result.getResponseMessage());
        }
        return result;
    }

    public Response execute(String route, String json) {
        Response result;
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
        if (serviceWorker == null) {
            Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
            logger.error("timer:Can not find route:".concat(route));
            result = null;
        } else {
            LocalWorkerContextImpl workerContext = new LocalWorkerContextImpl(this.sid, route, serviceWorker);
            workerContext.initWebsocketParameter(json);
            serviceWorker.doWork(workerContext);
            result = workerContext.getWorkerResponse();
            //
            AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
            accessLogger.log(route, sid, json, result.getResponseMessage());
        }
        return result;
    }
}
