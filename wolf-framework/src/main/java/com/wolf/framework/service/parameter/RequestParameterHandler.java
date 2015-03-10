package com.wolf.framework.service.parameter;

/**
 *
 * @author aladdin
 */
public interface RequestParameterHandler {

    public String getName();

    public String getDataType();

    public String validate(String value);
    
    public String convertToInput(String value);
}
