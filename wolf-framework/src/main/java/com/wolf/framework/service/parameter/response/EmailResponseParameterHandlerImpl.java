package com.wolf.framework.service.parameter.response;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * 邮箱类型
 *
 * @author aladdin
 */
public final class EmailResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    public EmailResponseParameterHandlerImpl(final String name) {
        super(name, DataType.EMAIL);
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
