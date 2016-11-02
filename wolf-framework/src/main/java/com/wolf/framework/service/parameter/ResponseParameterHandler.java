package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataType;

/**
 *
 * @author aladdin
 */
public interface ResponseParameterHandler {

    public String getName();

    public DataType getDataType();

    public String getJson(String value);
}
