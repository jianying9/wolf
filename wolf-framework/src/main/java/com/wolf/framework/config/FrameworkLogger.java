package com.wolf.framework.config;

import com.wolf.framework.logger.LoggerType;

/**
 *
 * @author aladdin
 */
public enum FrameworkLogger implements LoggerType{
    FRAMEWORK,
    DAO,
    LUCENE,
    WEBSOCKET;

    @Override
    public String getLoggerName() {
        return this.name();
    }
}
