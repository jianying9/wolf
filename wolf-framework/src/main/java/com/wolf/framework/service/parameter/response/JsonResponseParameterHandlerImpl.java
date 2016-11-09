package com.wolf.framework.service.parameter.response;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * json类型处理类
 *
 * @author aladdin
 */
public final class JsonResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    private final String defaultValue;

    public JsonResponseParameterHandlerImpl(String name, DataType dataType, String defaultValue) {
        super(name, dataType);
        this.defaultValue = defaultValue;
    }

    @Override
    public String getJson(String value) {
        String result;
        value = value.equals("") ? this.defaultValue : value;
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 3);
        jsonBuilder.append('"').append(name).append("\":").append(value);
        result = jsonBuilder.toString();
        return result;
    }
}
