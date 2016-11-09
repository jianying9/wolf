package com.wolf.framework.service.parameter.response;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;

/**
 * 中国手机号码类型处理类
 *
 * @author aladdin
 */
public final class ChinaMobileResponseParameterHandlerImpl extends AbstractResponseParameterHandler implements ResponseParameterHandler {

    public ChinaMobileResponseParameterHandlerImpl(final String name) {
        super(name, DataType.CHINA_MOBILE);
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
