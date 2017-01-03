package com.wolf.framework.service.parameter;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterType;
import com.wolf.framework.service.parameter.response.BooleanResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.DoubleResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.LongArrayResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.LongResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.StringArrayResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.StringResponseParameterHandlerImpl;

/**
 *
 * @author jianying9
 */
public class ThirdResponseParameterHandlerBuilder {

    private final ThirdResponseConfig thirdResponseConfig;

    public ThirdResponseParameterHandlerBuilder(final ThirdResponseConfig thirdResponseConfig) {
        this.thirdResponseConfig = thirdResponseConfig;
    }


    public ResponseParameterHandler build() {
        ResponseParameterHandler parameterHandler = null;
        final String fieldName = this.thirdResponseConfig.name();
        //
        final FilterFactory filterFactory = ApplicationContext.CONTEXT.getFilterFactory();
        Filter[] filters = null;
        //获取过滤对象
        FilterType[] filterTypeEnums = this.thirdResponseConfig.filterTypes();
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
        //基本数据类型
        ResponseDataType dataType = this.thirdResponseConfig.dataType();
        switch (dataType) {
            case STRING:
                parameterHandler = new StringResponseParameterHandlerImpl(fieldName, ResponseDataType.STRING, filters);
                break;
            case DATE:
                parameterHandler = new StringResponseParameterHandlerImpl(fieldName, ResponseDataType.DATE, filters);
                break;
            case DATE_TIME:
                parameterHandler = new StringResponseParameterHandlerImpl(fieldName, ResponseDataType.DATE_TIME, filters);
                break;
            case LONG:
                parameterHandler = new LongResponseParameterHandlerImpl(fieldName);
                break;
            case DOUBLE:
                parameterHandler = new DoubleResponseParameterHandlerImpl(fieldName);
                break;
            case BOOLEAN:
                parameterHandler = new BooleanResponseParameterHandlerImpl(fieldName);
                break;
            case ENUM:
                parameterHandler = new StringResponseParameterHandlerImpl(fieldName, ResponseDataType.ENUM, filters);
                break;
            case CHINA_MOBILE:
                parameterHandler = new StringResponseParameterHandlerImpl(fieldName, ResponseDataType.CHINA_MOBILE, filters);
                break;
            case EMAIL:
                parameterHandler = new StringResponseParameterHandlerImpl(fieldName, ResponseDataType.EMAIL, filters);
                break;
            case LONG_ARRAY:
                parameterHandler = new LongArrayResponseParameterHandlerImpl(fieldName);
                break;
            case STRING_ARRAY:
                parameterHandler = new StringArrayResponseParameterHandlerImpl(fieldName, filters);
                break;
        }
        return parameterHandler;
    }
}
