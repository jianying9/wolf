package com.wolf.framework.service.parameter;

/**
 *
 * @author aladdin
 */
public interface ParameterHandler {

    public String getName();
    
    public String getDesc();

    public String getJson(String value);

    public String validate(String value);

    public String getDefaultValue();

    public String getRandomValue();
    
    public String convertToInput(String value);
}
