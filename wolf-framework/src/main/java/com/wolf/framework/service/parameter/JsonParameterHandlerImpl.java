package com.wolf.framework.service.parameter;

/**
 * json类型处理类
 *
 * @author aladdin
 */
public final class JsonParameterHandlerImpl implements ParameterHandler {

    private final String name;
    private final String dataType;
    private final String desc;
    private final String defaultValue;

    public JsonParameterHandlerImpl(String name, String dataType, String desc, String defaultValue) {
        this.name = name;
        this.dataType = dataType;
        this.desc = desc;
        this.defaultValue = defaultValue.isEmpty() ? "{}" : defaultValue;
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
    public String getDescription() {
        return this.desc;
    }

    @Override
    public String validate(String value) {
        throw new UnsupportedOperationException("Json type only use in Output...");
    }

    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String getRandomValue() {
        return this.defaultValue;
    }

    @Override
    public String convertToInput(String value) {
        throw new UnsupportedOperationException("Json type only use in Output...");
    }

    @Override
    public String getDataType() {
        return this.dataType;
    }
}
