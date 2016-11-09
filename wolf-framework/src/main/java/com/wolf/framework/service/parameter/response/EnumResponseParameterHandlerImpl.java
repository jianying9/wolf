package com.wolf.framework.service.parameter.response;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * 枚举类型处理类
 *
 * @author aladdin
 */
public final class EnumResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    public EnumResponseParameterHandlerImpl(String name) {
        super(name, DataType.ENUM);
    }

    @Override
    public String getJson(String value) {
        String result;
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 5);
        jsonBuilder.append('"').append(name).append("\":\"").append(value).append("\"");
        result = jsonBuilder.toString();
        return result;
    }
}
