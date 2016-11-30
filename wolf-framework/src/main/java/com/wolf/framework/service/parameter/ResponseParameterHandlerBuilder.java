package com.wolf.framework.service.parameter;

import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterType;
import com.wolf.framework.service.parameter.response.BooleanResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.JsonResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.NumberResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.SimpleStringResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.StringResponseParameterHandlerImpl;

/**
 *
 * @author aladdin
 */
public class ResponseParameterHandlerBuilder {

    private final ResponseConfig outputConfig;
    private final ParameterContext parameterContext;

    public ResponseParameterHandlerBuilder(
            final ResponseConfig outputConfig,
            final ParameterContext parameterContext) {
        this.outputConfig = outputConfig;
        this.parameterContext = parameterContext;
    }

    public ResponseParameterHandler build() {
        ResponseParameterHandler parameterHandler = null;
        final String fieldName = this.outputConfig.name();
        //
        final FilterFactory filterFactory = this.parameterContext.getFilterFactory();
        //基本数据类型
        ResponseDataType dataType = this.outputConfig.dataType();
        switch (dataType) {
            case OBJECT:
                parameterHandler = new JsonResponseParameterHandlerImpl(fieldName, dataType, "{}");
                break;
            case ARRAY:
                parameterHandler = new JsonResponseParameterHandlerImpl(fieldName, dataType, "[]");
                break;
            case STRING:
                Filter[] filters = null;
                //获取过滤对象
                FilterType[] filterTypeEnums = this.outputConfig.filterTypes();
                if (filterTypeEnums.length > 0) {
                    Filter filter;
                    filters = new Filter[filterTypeEnums.length];
                    for (int index = 0; index < filterTypeEnums.length; index++) {
                        filter = filterFactory.getFilter(filterTypeEnums[index]);
                        if (filter == null) {
                            throw new RuntimeException("Error when building ResponseParameterHandler. Cause: could not find Filter.");
                        }
                        filters[index] = filter;
                    }
                }
                parameterHandler = new StringResponseParameterHandlerImpl(fieldName, filters);
                break;
            case DATE:
                parameterHandler = new SimpleStringResponseParameterHandlerImpl(fieldName, ResponseDataType.DATE);
                break;
            case DATE_TIME:
                parameterHandler = new SimpleStringResponseParameterHandlerImpl(fieldName, ResponseDataType.DATE_TIME);
                break;
            case LONG:
                parameterHandler = new NumberResponseParameterHandlerImpl(fieldName, ResponseDataType.LONG);
                break;
            case DOUBLE:
                parameterHandler = new NumberResponseParameterHandlerImpl(fieldName, ResponseDataType.DOUBLE);
                break;
            case BOOLEAN:
                parameterHandler = new BooleanResponseParameterHandlerImpl(fieldName);
                break;
            case ENUM:
                parameterHandler = new SimpleStringResponseParameterHandlerImpl(fieldName, ResponseDataType.ENUM);
                break;
            case CHINA_MOBILE:
                parameterHandler = new SimpleStringResponseParameterHandlerImpl(fieldName, ResponseDataType.CHINA_MOBILE);
                break;
            case EMAIL:
                parameterHandler = new SimpleStringResponseParameterHandlerImpl(fieldName, ResponseDataType.EMAIL);
                break;
        }
        return parameterHandler;
    }
}
