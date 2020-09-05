package com.wolf.framework.config;

import com.wolf.framework.logger.LoggerType;

/**
 *
 * @author aladdin
 */
public enum FrameworkLogger implements LoggerType {
    FRAMEWORK,
    ACCESS,
    DAO,
    LUCENE,
    XIAOMI,
    HUAWEI,
    APNS,
    HTTPCLIENT;

    @Override
    public String getLoggerName() {
        return this.name();
    }
}
