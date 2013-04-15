package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;

/**
 * 数字类型处理类
 *
 * @author aladdin
 */
public final class NumberParameterHandlerImpl extends AbstractParameterHandler implements ParameterHandler {

    public NumberParameterHandlerImpl(final String name, final DataHandler dataHandler, final String defaultValue, final String desc) {
        super(name, dataHandler, defaultValue, desc);
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
