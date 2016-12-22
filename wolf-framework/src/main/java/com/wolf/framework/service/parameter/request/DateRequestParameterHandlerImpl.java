package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestParameterHandler;

/**
 * 时间类型处理类
 *
 * @author aladdin
 */
public final class DateRequestParameterHandlerImpl extends AbstractRegexRequestParameterHandler implements RequestParameterHandler {
    
    public DateRequestParameterHandlerImpl(final String name) {
        super(name, RequestDataType.DATE, "[1-9]\\d{3}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|3[0-1])", " must be date[yyyy-mm-dd]");
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
