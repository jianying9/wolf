package com.wolf.framework.service.parameter;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterType;
import com.wolf.framework.service.parameter.response.BooleanResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.DoubleResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.LongArrayResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.LongResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.ObjectArrayResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.ObjectResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.StringArrayResponseParameterHandlerImpl;
import com.wolf.framework.service.parameter.response.StringResponseParameterHandlerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ResponseParameterHandlerBuilder {

    private final ResponseConfig responseConfig;

    public ResponseParameterHandlerBuilder(final ResponseConfig ResponseConfig) {
        this.responseConfig = ResponseConfig;
    }

    private ObjectResponseHandlerInfo createObjectHandlerInfo(SecondResponseConfig[] secondResponseConfigs) {
        ResponseParameterHandler requestParameterHandler;
        SecondResponseParameterHandlerBuilder responseParameterHandlerBuilder;
        final Map<String, ResponseParameterHandler> responseParameterMap = new HashMap<>(secondResponseConfigs.length, 1);
        //
        List<String> nameList = new ArrayList<>(secondResponseConfigs.length);
        for (SecondResponseConfig secondRequestConfig : secondResponseConfigs) {
            responseParameterHandlerBuilder = new SecondResponseParameterHandlerBuilder(secondRequestConfig);
            requestParameterHandler = responseParameterHandlerBuilder.build();
            if (requestParameterHandler != null) {
                responseParameterMap.put(secondRequestConfig.name(), requestParameterHandler);
                nameList.add(secondRequestConfig.name());
            }
        }
        final String[] names = nameList.toArray(new String[nameList.size()]);
        //
        ObjectResponseHandlerInfo objectHandlerInfo = new ObjectResponseHandlerInfo(names, responseParameterMap);
        return objectHandlerInfo;
    }

    public ResponseParameterHandler build() {
        ResponseParameterHandler parameterHandler = null;
        final String fieldName = this.responseConfig.name();
        //
        final FilterFactory filterFactory = ApplicationContext.CONTEXT.getFilterFactory();
        Filter[] filters = null;
        //获取过滤对象
        FilterType[] filterTypeEnums = this.responseConfig.filterTypes();
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
        ResponseDataType dataType = this.responseConfig.dataType();
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
            case OBJECT:
                SecondResponseConfig[] secondResponseConfigs = this.responseConfig.secondResponseConfigs();
                if(secondResponseConfigs.length > 0) {
                    ObjectResponseHandlerInfo objectHandlerInfo = this.createObjectHandlerInfo(secondResponseConfigs);
                    parameterHandler = new ObjectResponseParameterHandlerImpl(fieldName, objectHandlerInfo);
                }
                break;
            case OBJECT_ARRAY:
                secondResponseConfigs = this.responseConfig.secondResponseConfigs();
                if(secondResponseConfigs.length > 0) {
                    ObjectResponseHandlerInfo objectHandlerInfo = this.createObjectHandlerInfo(secondResponseConfigs);
                    parameterHandler = new ObjectArrayResponseParameterHandlerImpl(fieldName, objectHandlerInfo);
                }
                break;
        }
        return parameterHandler;
    }
}
