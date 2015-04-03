package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;

/**
 * parameter处理抽象类
 *
 * @author aladdin
 */
public abstract class AbstractParameterHandler {

    protected final String name;
    protected final DataHandler dataHandler;

    protected AbstractParameterHandler(final String name, final DataHandler dataHandler) {
        this.name = name;
        this.dataHandler = dataHandler;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDataType() {
        return this.dataHandler.getDataType().name();
    }

    public final DataHandler getDataHandler() {
        return this.dataHandler;
    }
}
