package com.wolf.framework.logger;

import com.wolf.framework.config.FrameworkLogger;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public final class AccessLoggerDefaultImpl implements AccessLogger {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.ACCESS);

    @Override
    public void log(String route, String sid, String request, String response) {
        this.logger.info("sid:{}", sid);
        this.logger.info("request:{}", request);
        this.logger.info("response:{}", response);
    }

    @Override
    public void log(String sid, String type, String operate) {
        this.logger.info("sid[" + sid + "]:" + type + " " + operate);
    }

}
