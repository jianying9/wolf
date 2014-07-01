package com.wolf.framework.service.parameter;

/**
 *
 * @author aladdin
 */
public interface ResponseParameterHandler {

    public String getName();

    public String getDataType();

    public String getJson(String value);

    public String getRandomValue();
}
