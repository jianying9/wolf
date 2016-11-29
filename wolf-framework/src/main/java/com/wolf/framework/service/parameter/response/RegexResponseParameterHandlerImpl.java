package com.wolf.framework.service.parameter.response;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * 正则类型处理类
 *
 * @author aladdin
 */
public final class RegexResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    public RegexResponseParameterHandlerImpl(String name) {
        super(name, DataType.REGEX);
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
