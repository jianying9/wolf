package com.wolf.framework.service.parameter;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterType;
import com.wolf.framework.service.parameter.response.BooleanResponseHandlerImpl;
import com.wolf.framework.service.parameter.response.DoubleResponseHandlerImpl;
import com.wolf.framework.service.parameter.response.LongArrayResponseHandlerImpl;
import com.wolf.framework.service.parameter.response.LongResponseHandlerImpl;
import com.wolf.framework.service.parameter.response.ObjectArrayResponseHandlerImpl;
import com.wolf.framework.service.parameter.response.ObjectResponseHandlerImpl;
import com.wolf.framework.service.parameter.response.StringArrayResponseHandlerImpl;
import com.wolf.framework.service.parameter.response.StringResponseHandlerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ResponseHandlerBuilder {

    private final ResponseInfo responseInfo;

    public ResponseHandlerBuilder(final ResponseInfo ResponseInfo) {
        this.responseInfo = ResponseInfo;
    }

    private ObjectResponseHandlerInfo createObjectHandlerInfo(List<ResponseInfo> childResponseInfoList) {
        ResponseHandler requestHandler;
        ResponseHandlerBuilder responseHandlerBuilder;
        final Map<String, ResponseHandler> responseHandlerMap = new HashMap(childResponseInfoList.size(), 1);
        //
        List<String> nameList = new ArrayList(childResponseInfoList.size());
        for (ResponseInfo childResponseInfo : childResponseInfoList) {
            responseHandlerBuilder = new ResponseHandlerBuilder(childResponseInfo);
            requestHandler = responseHandlerBuilder.build();
            if (requestHandler != null) {
                responseHandlerMap.put(childResponseInfo.getName(), requestHandler);
                nameList.add(childResponseInfo.getName());
            }
        }
        final String[] names = nameList.toArray(new String[nameList.size()]);
        //
        ObjectResponseHandlerInfo objectHandlerInfo = new ObjectResponseHandlerInfo(names, responseHandlerMap);
        return objectHandlerInfo;
    }

    public ResponseHandler build() {
        ResponseHandler responseHandler = null;
        final String fieldName = this.responseInfo.getName();
        //
        final FilterFactory filterFactory = ApplicationContext.CONTEXT.getFilterFactory();
        Filter[] filters = null;
        //获取过滤对象
        FilterType[] filterTypes = this.responseInfo.getFilterTypes();
        if (filterTypes.length > 0) {
            Filter filter;
            filters = new Filter[filterTypes.length];
            for (int index = 0; index < filterTypes.length; index++) {
                filter = filterFactory.getFilter(filterTypes[index]);
                if (filter == null) {
                    throw new RuntimeException("Error when building ResponseParameterHandler. Cause: could not find Filter.");
                }
                filters[index] = filter;
            }
        }
        //基本数据类型
        ResponseDataType dataType = this.responseInfo.getDataType();
        switch (dataType) {
            case STRING:
                responseHandler = new StringResponseHandlerImpl(fieldName, ResponseDataType.STRING, filters);
                break;
            case DATE:
                responseHandler = new StringResponseHandlerImpl(fieldName, ResponseDataType.DATE, filters);
                break;
            case DATE_TIME:
                responseHandler = new StringResponseHandlerImpl(fieldName, ResponseDataType.DATE_TIME, filters);
                break;
            case LONG:
                responseHandler = new LongResponseHandlerImpl(fieldName);
                break;
            case DOUBLE:
                responseHandler = new DoubleResponseHandlerImpl(fieldName);
                break;
            case BOOLEAN:
                responseHandler = new BooleanResponseHandlerImpl(fieldName);
                break;
            case ENUM:
                responseHandler = new StringResponseHandlerImpl(fieldName, ResponseDataType.ENUM, filters);
                break;
            case CHINA_MOBILE:
                responseHandler = new StringResponseHandlerImpl(fieldName, ResponseDataType.CHINA_MOBILE, filters);
                break;
            case EMAIL:
                responseHandler = new StringResponseHandlerImpl(fieldName, ResponseDataType.EMAIL, filters);
                break;
            case LONG_ARRAY:
                responseHandler = new LongArrayResponseHandlerImpl(fieldName);
                break;
            case STRING_ARRAY:
                responseHandler = new StringArrayResponseHandlerImpl(fieldName, filters);
                break;
            case OBJECT:
                List<ResponseInfo> childResponseInfoList = this.responseInfo.getChildList();
                if(childResponseInfoList.isEmpty() == false) {
                    ObjectResponseHandlerInfo objectHandlerInfo = this.createObjectHandlerInfo(childResponseInfoList);
                    responseHandler = new ObjectResponseHandlerImpl(fieldName, objectHandlerInfo);
                }
                break;
            case OBJECT_ARRAY:
                childResponseInfoList = this.responseInfo.getChildList();
                if(childResponseInfoList.isEmpty() == false) {
                    ObjectResponseHandlerInfo objectHandlerInfo = this.createObjectHandlerInfo(childResponseInfoList);
                    responseHandler = new ObjectArrayResponseHandlerImpl(fieldName, objectHandlerInfo);
                }
                break;
        }
        return responseHandler;
    }
}
