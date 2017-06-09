package com.wolf.framework.service.parameter;

import com.wolf.framework.service.parameter.request.BooleanRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.ChinaMobileRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.DateRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.DateTimeRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.DoubleRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.EmailRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.EnumRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.LongArrayRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.LongRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.ObjectArrayRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.ObjectRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.RegexRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.StringArrayRequestHandlerImpl;
import com.wolf.framework.service.parameter.request.StringRequestHandlerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class RequestHandlerBuilder {

    private final RequestInfo requestInfo;

    public RequestHandlerBuilder(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    private ObjectRequestHandlerInfo createObjectHandlerInfo(List<RequestInfo> childRequestInfoList) {
        final List<RequestInfo> requiredRequestInfoList = new ArrayList(0);
        final List<RequestInfo> unrequiredRequestInfoList = new ArrayList(0);
        for (RequestInfo childRequestInfo : childRequestInfoList) {
            if (childRequestInfo.isRequired()) {
                requiredRequestInfoList.add(childRequestInfo);
            } else {
                unrequiredRequestInfoList.add(childRequestInfo);
            }
        }
        RequestHandler childRequestHandler;
        RequestHandlerBuilder requestHandlerBuilder;
        final Map<String, RequestHandler> requestHandlerMap = new HashMap(childRequestInfoList.size(), 1);
        List<String> unrequiredNameList = new ArrayList(unrequiredRequestInfoList.size());
        for (RequestInfo childRequestInfo : unrequiredRequestInfoList) {
            requestHandlerBuilder = new RequestHandlerBuilder(childRequestInfo);
            childRequestHandler = requestHandlerBuilder.build();
            if (childRequestHandler != null) {
                requestHandlerMap.put(childRequestInfo.getName(), childRequestHandler);
                unrequiredNameList.add(childRequestInfo.getName());
            }
        }
        final String[] unrequiredNames = unrequiredNameList.toArray(new String[unrequiredNameList.size()]);
        //
        List<String> requiredNameList = new ArrayList(requiredRequestInfoList.size());
        for (RequestInfo childRequestInfo : requiredRequestInfoList) {
            requestHandlerBuilder = new RequestHandlerBuilder(childRequestInfo);
            childRequestHandler = requestHandlerBuilder.build();
            if (childRequestHandler != null) {
                requestHandlerMap.put(childRequestInfo.getName(), childRequestHandler);
                requiredNameList.add(childRequestInfo.getName());
            }
        }
        final String[] requiredNames = requiredNameList.toArray(new String[requiredNameList.size()]);
        //
        ObjectRequestHandlerInfo objectHandlerInfo = new ObjectRequestHandlerInfo(requiredNames, unrequiredNames, requestHandlerMap);
        return objectHandlerInfo;
    }

    public RequestHandler build() {
        RequestHandler requestHandler = null;
        final String fieldName = this.requestInfo.getName();
        //基本数据类型
        RequestDataType dataType = this.requestInfo.getDataType();
        long max = this.requestInfo.getMax();
        long min = this.requestInfo.getMin();
        boolean ignoreEmpty = this.requestInfo.isIgnoreEmpty();
        String text = this.requestInfo.getText();
        switch (dataType) {
            case STRING:
                requestHandler = new StringRequestHandlerImpl(fieldName, max, min, ignoreEmpty);
                break;
            case DATE:
                requestHandler = new DateRequestHandlerImpl(fieldName, ignoreEmpty);
                break;
            case DATE_TIME:
                requestHandler = new DateTimeRequestHandlerImpl(fieldName, ignoreEmpty);
                break;
            case LONG:
                requestHandler = new LongRequestHandlerImpl(fieldName, max, min, ignoreEmpty);
                break;
            case DOUBLE:
                requestHandler = new DoubleRequestHandlerImpl(fieldName, max, min, ignoreEmpty);
                break;
            case BOOLEAN:
                requestHandler = new BooleanRequestHandlerImpl(fieldName, ignoreEmpty);
                break;
            case ENUM:
                String[] enumValues = text.split(",");
                requestHandler = new EnumRequestHandlerImpl(fieldName, enumValues, ignoreEmpty);
                break;
            case REGEX:
                requestHandler = new RegexRequestHandlerImpl(fieldName, text, ignoreEmpty);
                break;
            case CHINA_MOBILE:
                requestHandler = new ChinaMobileRequestHandlerImpl(fieldName, ignoreEmpty);
                break;
            case EMAIL:
                requestHandler = new EmailRequestHandlerImpl(fieldName, ignoreEmpty);
                break;
            case LONG_ARRAY:
                requestHandler = new LongArrayRequestHandlerImpl(fieldName, ignoreEmpty);
                break;
            case STRING_ARRAY:
                requestHandler = new StringArrayRequestHandlerImpl(fieldName, ignoreEmpty);
                break;
            case OBJECT:
                List<RequestInfo> childRequestInfoList = this.requestInfo.getChildList();
                if (childRequestInfoList.isEmpty() == false) {
                    ObjectRequestHandlerInfo objectRequestHandlerInfo = this.createObjectHandlerInfo(childRequestInfoList);
                    requestHandler = new ObjectRequestHandlerImpl(fieldName, ignoreEmpty, objectRequestHandlerInfo);
                } else {
                    requestHandler = null;
                }
                break;
            case OBJECT_ARRAY:
                childRequestInfoList = this.requestInfo.getChildList();
                if (childRequestInfoList.isEmpty() == false) {
                    ObjectRequestHandlerInfo objectRequestHandlerInfo = this.createObjectHandlerInfo(childRequestInfoList);
                    requestHandler = new ObjectArrayRequestHandlerImpl(fieldName, ignoreEmpty, objectRequestHandlerInfo);
                } else {
                    requestHandler = null;
                }
                break;
        }
        return requestHandler;
    }
}
