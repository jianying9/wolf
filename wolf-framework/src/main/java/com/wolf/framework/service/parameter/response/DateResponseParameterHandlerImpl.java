package com.wolf.framework.service.parameter.response;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * 时间类型处理类
 *
 * @author aladdin
 */
public final class DateResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    public DateResponseParameterHandlerImpl(final String name, final DataType dataType) {
        super(name, dataType);
    }

    @Override
    public String getJson(String value) {
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 5);
        jsonBuilder.append('"').append(this.name).append("\":\"").append(value).append('"');
        return jsonBuilder.toString();
    }
}
