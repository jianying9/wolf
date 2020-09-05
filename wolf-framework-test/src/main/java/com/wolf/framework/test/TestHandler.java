package com.wolf.framework.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.ApplicationContextBuilder;
import com.wolf.framework.logger.AccessLogger;
import com.wolf.framework.logger.AccessLoggerDefaultImpl;
import com.wolf.framework.logger.AccessLoggerFactory;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.LocalWorkerContextImpl;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author aladdin
 */
public final class TestHandler {

    private String sid;

    public TestHandler(Map<String, String> parameterMap) {
        synchronized (TestHandler.class) {
            if (ApplicationContext.CONTEXT.isReady() == false) {
                ApplicationContextBuilder applicationContextBuilder = new ApplicationContextBuilder(parameterMap);
                applicationContextBuilder.build();
            }
        }
    }

    public void setSessionId(String sid) {
        this.sid = sid;
    }

    public Response execute(String route, Map<String, Object> parameterMap) {
        parameterMap.put("pretty", true);
        Response result;
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
        if (serviceWorker == null) {
            Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
            logger.error("timer:Can not find route:".concat(route));
            result = null;
        } else {
            long start = System.currentTimeMillis();
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
            long time = System.currentTimeMillis() - start;
            AccessLogger accessLogger = new AccessLoggerDefaultImpl();
            accessLogger.log(route, sid, json, result.getResponseMessage(), time);
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
            long start = System.currentTimeMillis();
            LocalWorkerContextImpl workerContext = new LocalWorkerContextImpl(this.sid, route, serviceWorker);
            workerContext.initWebsocketParameter(json);
            serviceWorker.doWork(workerContext);
            result = workerContext.getWorkerResponse();
            //
            long time = System.currentTimeMillis() - start;
            AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
            accessLogger.log(route, sid, json, result.getResponseMessage(), time);
        }
        return result;
    }
}
