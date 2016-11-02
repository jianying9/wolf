package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataType;

/**
 *
 * @author aladdin
 */
public interface RequestParameterHandler {

    public String getName();

    public DataType getDataType();

    public String validate(String value);
}
