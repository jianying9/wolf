package com.demo.config;

/**
 *
 * @author jianying9
 */
public enum LoggerType implements com.wolf.framework.logger.LoggerType{
    

    TIMER;

    @Override
    public String getLoggerName() {
        String name = this.name();
        if (name.contains("_")) {
            name = name.replace("_", ".");
        }
        return name;
    }
    
}
