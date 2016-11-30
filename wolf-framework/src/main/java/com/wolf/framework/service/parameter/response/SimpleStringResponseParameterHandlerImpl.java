package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * 简单字符类型处理类
 *
 * @author aladdin
 */
public final class SimpleStringResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    public SimpleStringResponseParameterHandlerImpl(final String name, final ResponseDataType dataType) {
        super(name, dataType);
    }

    @Override
    public String getJson(String value) {
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 5);
        jsonBuilder.append('"').append(this.name).append("\":\"").append(value).append('"');
        return jsonBuilder.toString();
    }
}
