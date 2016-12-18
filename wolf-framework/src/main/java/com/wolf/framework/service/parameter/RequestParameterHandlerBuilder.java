package com.wolf.framework.service.parameter;

import com.wolf.framework.service.parameter.request.BooleanRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.ChinaMobileRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.DateRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.DateTimeRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.DoubleRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.EmailRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.EnumRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.LongRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.RegexRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.StringRequestParameterHandlerImpl;

/**
 *
 * @author aladdin
 */
public class RequestParameterHandlerBuilder {

    private final RequestConfig requestConfig;

    public RequestParameterHandlerBuilder(final RequestConfig inputConfig) {
        this.requestConfig = inputConfig;
    }

    public RequestParameterHandler build() {
        RequestParameterHandler parameterHandler = null;
        final String fieldName = this.requestConfig.name();
        //
        //基本数据类型
        RequestDataType dataType = this.requestConfig.dataType();
        long max = this.requestConfig.max();
        long min = this.requestConfig.min();
        String text = this.requestConfig.text();
        switch (dataType) {
            case STRING:
                parameterHandler = new StringRequestParameterHandlerImpl(fieldName, max, min);
                break;
            case DATE:
                parameterHandler = new DateRequestParameterHandlerImpl(fieldName);
                break;
            case DATE_TIME:
                parameterHandler = new DateTimeRequestParameterHandlerImpl(fieldName);
                break;
            case LONG:
                parameterHandler = new LongRequestParameterHandlerImpl(fieldName, max, min);
                break;
            case DOUBLE:
                parameterHandler = new DoubleRequestParameterHandlerImpl(fieldName, max, min);
                break;
            case BOOLEAN:
                parameterHandler = new BooleanRequestParameterHandlerImpl(fieldName);
                break;
            case ENUM:
                String[] enumValues = text.split(",");
                parameterHandler = new EnumRequestParameterHandlerImpl(fieldName, enumValues);
                break;
            case REGEX:
                parameterHandler = new RegexRequestParameterHandlerImpl(fieldName, text);
                break;
            case CHINA_MOBILE:
                parameterHandler = new ChinaMobileRequestParameterHandlerImpl(fieldName);
                break;
            case EMAIL:
                parameterHandler = new EmailRequestParameterHandlerImpl(fieldName);
                break;
        }
        return parameterHandler;
    }
}
