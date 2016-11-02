package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataType;

/**
 * json类型处理类
 *
 * @author aladdin
 */
public final class JsonParameterHandlerImpl implements ResponseParameterHandler {

    private final String name;
    private final DataType dataType;
    private final String defaultValue;

    public JsonParameterHandlerImpl(String name, DataType dataType, String defaultValue) {
        this.name = name;
        this.dataType = dataType;
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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DataType getDataType() {
        return this.dataType;
    }
}
