package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;

/**
 * 时间类型处理类
 *
 * @author aladdin
 */
public final class DateParameterHandlerImpl extends AbstractParameterHandler implements RequestParameterHandler, ResponseParameterHandler {

    DateParameterHandlerImpl(final String name, final DataHandler dataHandler) {
        super(name, dataHandler);
    }

    @Override
    public String validate(final String value) {
        boolean result = this.dataHandler.validate(value);
        return result ? "" : this.dataHandler.getErrorInfo();
    }

    @Override
    public String getJson(String value) {
        value = this.convertToOutput(value);
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 5);
        jsonBuilder.append('"').append(this.name).append("\":\"").append(value).append('"');
        return jsonBuilder.toString();
    }
}
