package com.wolf.framework.test;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.ApplicationContextBuilder;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.session.Session;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.LocalMessageContextImpl;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public final class TestHandler {

    private Session session;

    public TestHandler(Properties properties) {
        synchronized (TestHandler.class) {
            if (ApplicationContext.CONTEXT.isReady() == false) {
                properties.put("compileModel", FrameworkConfig.UNIT_TEST);
                ApplicationContextBuilder applicationContextBuilder = new ApplicationContextBuilder(properties);
                applicationContextBuilder.build();
            }
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String execute(String act, Map<String, String> parameterMap) {
        String result = "";
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(act);
        if (serviceWorker == null) {
            Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
            logger.error("timer:Can not find act:".concat(act));
            result = "{\"flag\":\"INVALID\",\"error\":\"act not exists\"}";
        } else {
            LocalMessageContextImpl localMessageContextImpl = new LocalMessageContextImpl(this.session, act, parameterMap);
            serviceWorker.doWork(localMessageContextImpl);
            result = localMessageContextImpl.getResponseMessage();
        }
        return result;
    }
}
