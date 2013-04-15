package com.wolf.framework.service.parameter;

/**
 * json类型处理类
 *
 * @author aladdin
 */
public final class JsonParameterHandlerImpl implements ParameterHandler {

    private final String name;
    private final String desc;
    private final String defaultValue;

    public JsonParameterHandlerImpl(String name, String desc, String defaultValue) {
        this.name = name;
        this.desc = desc;
        this.defaultValue = defaultValue.equals("") ? "{}" : defaultValue;
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

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public String validate(String value) {
        throw new UnsupportedOperationException("Only use in Output...");
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getRandomValue() {
        return this.defaultValue;
    }

    public String convertToInput(String value) {
        throw new UnsupportedOperationException("Only use in Output...");
    }
}
