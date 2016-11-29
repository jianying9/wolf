package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataType;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterType;
import com.wolf.framework.service.parameter.response.BooleanResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.ChinaMobileResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.DateResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.EmailResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.EnumResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.JsonResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.NumberResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.RegexResponseParameterHandlerImpl;
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
        //
        final DataHandlerFactory dataHandlerFactory = this.parameterContext.getDataHandlerFactory();
        //基本数据类型
        DataType dataType = this.outputConfig.dataType();
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
                            throw new RuntimeException("Error when building FieldHandler. Cause: could not find Filter.");
                        }
                        filters[index] = filter;
                    }
                }
                parameterHandler = new StringResponseParameterHandlerImpl(fieldName, filters);
                break;
            case DATE:
                parameterHandler = new DateResponseParameterHandlerImpl(fieldName, DataType.DATE);
                break;
            case DATE_TIME:
                parameterHandler = new DateResponseParameterHandlerImpl(fieldName, DataType.DATE_TIME);
                break;
            case LONG:
                parameterHandler = new NumberResponseParameterHandlerImpl(fieldName, DataType.LONG);
                break;
            case DOUBLE:
                parameterHandler = new NumberResponseParameterHandlerImpl(fieldName, DataType.DOUBLE);
                break;
            case BOOLEAN:
                parameterHandler = new BooleanResponseParameterHandlerImpl(fieldName);
                break;
            case ENUM:
                parameterHandler = new EnumResponseParameterHandlerImpl(fieldName);
                break;
            case REGEX:
                parameterHandler = new RegexResponseParameterHandlerImpl(fieldName);
                break;
            case CHINA_MOBILE:
                parameterHandler = new ChinaMobileResponseParameterHandlerImpl(fieldName);
                break;
            case EMAIL:
                parameterHandler = new EmailResponseParameterHandlerImpl(fieldName);
                break;
        }
        return parameterHandler;
    }
}
