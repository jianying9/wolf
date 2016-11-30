package com.wolf.framework.service.parameter;

/**
 *
 * @author aladdin
 */
public interface ResponseParameterHandler {

    public String getName();

    public ResponseDataType getDataType();

    public String getJson(String value);
}
