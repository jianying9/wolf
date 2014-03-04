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
    protected final String defaultValue;

    protected AbstractParameterHandler(final String name, final DataHandler dataHandler, final String defaultValue, final String description) {
        this.name = name;
        this.description = description;
        this.dataHandler = dataHandler;
        if (defaultValue.isEmpty()) {
            this.defaultValue = this.dataHandler.getDefaultValue();
        } else {
            this.defaultValue = defaultValue;
        }
    }

    public final String getDefaultValue() {
        return this.defaultValue;
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
