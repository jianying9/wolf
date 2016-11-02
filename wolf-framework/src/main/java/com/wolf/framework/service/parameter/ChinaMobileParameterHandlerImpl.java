package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;

/**
 * 中国手机号码类型处理类
 *
 * @author aladdin
 */
public final class ChinaMobileParameterHandlerImpl extends AbstractParameterHandler implements RequestParameterHandler, ResponseParameterHandler {

    public ChinaMobileParameterHandlerImpl(final String name, final DataHandler dataHandler) {
        super(name, dataHandler);
    }

    @Override
    public String getJson(String value) {
        String result;
        StringBuilder jsonBuilder = new StringBuilder(this.name.length() + value.length() + 5);
        jsonBuilder.append('"').append(name).append("\":\"").append(value).append("\"");
        result = jsonBuilder.toString();
        return result;
    }

    @Override
    public String validate(String value) {
        boolean result = this.dataHandler.validate(value);
        return result ? "" : this.dataHandler.getErrorInfo();
    }
}
