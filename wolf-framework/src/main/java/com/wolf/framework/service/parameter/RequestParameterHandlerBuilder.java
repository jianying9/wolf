package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.request.BooleanRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.ChinaMobileRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.DateRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.EmailRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.EnumRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.NumberRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.StringRequestParameterHandlerImpl;

/**
 *
 * @author aladdin
 */
public class RequestParameterHandlerBuilder {

    private final RequestConfig requestConfig;
    private final ParameterContext parameterContext;

    public RequestParameterHandlerBuilder(
            final RequestConfig inputConfig,
            final ParameterContext parameterContext) {
        this.requestConfig = inputConfig;
        this.parameterContext = parameterContext;
        
    }

    public RequestParameterHandler build() {
        RequestParameterHandler parameterHandler = null;
        final String fieldName = this.requestConfig.name();
        //
        final DataHandlerFactory dataHandlerFactory = this.parameterContext.getDataHandlerFactory();
        //基本数据类型
        DataType dataType = this.requestConfig.dataType();
        long max = this.requestConfig.max();
        long min = this.requestConfig.min();
        DataHandler dataHandler = dataHandlerFactory.getDataHandler(dataType);
        switch (dataType) {
            case OBJECT:
                throw new RuntimeException("Cause: Request parameter cannot support JSON OBJECT");
            case ARRAY:
                throw new RuntimeException("Cause: Request parameter cannot support JSON ARRAY");
            case STRING:
                parameterHandler = new StringRequestParameterHandlerImpl(fieldName, max, min);
                break;
            case DATE:
                parameterHandler = new DateRequestParameterHandlerImpl(fieldName, dataHandler);
                break;
            case DATE_TIME:
                parameterHandler = new DateRequestParameterHandlerImpl(fieldName, dataHandler);
                break;
            case LONG:
                parameterHandler = new NumberRequestParameterHandlerImpl(fieldName, dataHandler, max, min);
                break;
            case DOUBLE:
                parameterHandler = new NumberRequestParameterHandlerImpl(fieldName, dataHandler, max, min);
                break;
            case BOOLEAN:
                parameterHandler = new BooleanRequestParameterHandlerImpl(fieldName, dataHandler);
                break;
            case ENUM:
                String text = this.requestConfig.text();
                String[] enumValues = text.split("|");
                parameterHandler = new EnumRequestParameterHandlerImpl(fieldName, enumValues);
                break;
            case CHINA_MOBILE:
                parameterHandler = new ChinaMobileRequestParameterHandlerImpl(fieldName, dataHandler);
                break;
            case EMAIL:
                parameterHandler = new EmailRequestParameterHandlerImpl(fieldName, dataHandler);
                break;
        }
        return parameterHandler;
    }
}
