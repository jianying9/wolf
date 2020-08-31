package com.wolf.framework.logger;

/**
 *
 * @author aladdin
 */
public final class AccessLoggerFactory {

    private static AccessLogger ACCESS_LOGGER = new AccessLoggerDefaultImpl();

    public static void setAccessLogger(AccessLogger accessLogger) {
        ACCESS_LOGGER = accessLogger;
    }

    public static AccessLogger getAccessLogger() {
        return ACCESS_LOGGER;
    }
}
