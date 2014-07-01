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
    public String getJson(String value) {
        if (value.equals(DataHandler.DEFAULT_DATE_VALUE)) {
            value = "";
        } else {
            value = this.dataHandler.convertToOutput(value);
        }
        String result;
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 5);
        jsonBuilder.append('"').append(this.name).append("\":\"").append(value).append('"');
        result = jsonBuilder.toString();
        return result;
    }
}
