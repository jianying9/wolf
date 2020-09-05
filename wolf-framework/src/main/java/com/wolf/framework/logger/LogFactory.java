package com.wolf.framework.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 根据LoggerNameEnum枚举,获取对应的logger对象.在使用之前,请先在LoggerNameEnum中维护每个logger的枚举,以及该枚举对应的name
 *
 * @author aladdin
 */
public final class LogFactory
{

    /**
     * 根据LoggerNameEnum获取对应的logger对象
     *
     * @param loggerType
     * @return
     */
    public static Logger getLogger(LoggerType loggerType)
    {
        return LogManager.getLogger(loggerType.getLoggerName());
    }
}
