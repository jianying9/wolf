package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.*;

/**
 * 数字类型处理类
 *
 * @author aladdin
 */
public final class NumberResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    public NumberResponseParameterHandlerImpl(final String name, ResponseDataType dataType) {
        super(name, dataType);
    }

    @Override
    public String getJson(final String value) {
        String result;
        StringBuilder jsonBuilder = new StringBuilder(this.getName().length() + value.length() + 3);
        jsonBuilder.append('"').append(this.getName()).append("\":").append(value);
        result = jsonBuilder.toString();
        return result;
    }
}
