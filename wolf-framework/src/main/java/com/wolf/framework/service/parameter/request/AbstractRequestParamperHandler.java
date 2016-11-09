package com.wolf.framework.service.parameter.request;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * parameter处理抽象类
 *
 * @author aladdin
 */
public abstract class AbstractRequestParamperHandler implements RequestParameterHandler {

    protected final String name;
    protected final DataHandler dataHandler;

    protected AbstractRequestParamperHandler(final String name, final DataHandler dataHandler) {
        this.name = name;
        this.dataHandler = dataHandler;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final DataType getDataType() {
        return this.dataHandler.getDataType();
    }

    public final DataHandler getDataHandler() {
        return this.dataHandler;
    }
}
