package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataType;

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

    public final DataType getDataType() {
        return this.dataHandler.getDataType();
    }

    public final DataHandler getDataHandler() {
        return this.dataHandler;
    }
}
