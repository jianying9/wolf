package com.wolf.framework.service.parameter.request;

import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestHandler;

/**
 * 时间类型处理类
 *
 * @author aladdin
 */
public final class DateTimeRequestHandlerImpl extends AbstractRegexRequestHandler implements RequestHandler {
    
    public DateTimeRequestHandlerImpl(final String name, boolean ignoreEmpty) {
        super(name, RequestDataType.DATE_TIME, "[1-9]\\d{3}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|3[0-1]) (?:[0-1]\\d|2[0-3]):[0-5]\\d(?::[0-5]\\d)?\")", " must be date[yyyy-mm-dd hh:mi:ss]", ignoreEmpty);
    }
}
