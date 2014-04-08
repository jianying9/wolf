package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;

/**
 * parameter处理抽象类
 *
 * @author aladdin
 */
public abstract class AbstractParameterHandler {

    protected final String name;
    protected final String description;
    protected final DataHandler dataHandler;

    protected AbstractParameterHandler(final String name, final DataHandler dataHandler, final String description) {
        this.name = name;
        this.description = description;
        this.dataHandler = dataHandler;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDataType() {
        return this.dataHandler.getDataTypeEnum().name();
    }

    public final String getDescription() {
        return this.description;
    }

    public final DataHandler getDataHandler() {
        return this.dataHandler;
    }

    public String validate(final String value) {
        return this.dataHandler.validate(value);
    }

    public String getRandomValue() {
        return this.dataHandler.getRandomValue();
    }

    public String convertToInput(String value) {
        return this.dataHandler.convertToInput(value);
    }
}
