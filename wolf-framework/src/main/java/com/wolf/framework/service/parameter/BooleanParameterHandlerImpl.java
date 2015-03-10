package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;

/**
 * boolean类型处理类
 *
 * @author aladdin
 */
public final class BooleanParameterHandlerImpl extends AbstractParameterHandler implements RequestParameterHandler, ResponseParameterHandler {

    public BooleanParameterHandlerImpl(final String name, final DataHandler dataHandler) {
        super(name, dataHandler);
    }

    @Override
    public String validate(final String value) {
        boolean result = this.dataHandler.validate(value);
        return result ? "" : this.dataHandler.getErrorInfo();
    }

    @Override
    public String getJson(final String value) {
        String result;
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 3);
        jsonBuilder.append('"').append(this.name).append("\":").append(value);
        result = jsonBuilder.toString();
        return result;
    }
}
