package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * boolean类型处理类
 *
 * @author aladdin
 */
public final class BooleanResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    public BooleanResponseParameterHandlerImpl(final String name) {
        super(name, ResponseDataType.BOOLEAN);
    }

    @Override
    public String getJson(final String value) {
        String result;
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 3);
        jsonBuilder.append('"').append(this.name).append("\":").append(value);
        result = jsonBuilder.toString();
        return result;
    }
}
