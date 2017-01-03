package com.wolf.framework.service.parameter;

import com.wolf.framework.service.parameter.request.BooleanRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.ChinaMobileRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.DateRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.DateTimeRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.DoubleRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.EmailRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.EnumRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.LongArrayRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.LongRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.ObjectArrayRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.ObjectRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.RegexRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.StringArrayRequestParameterHandlerImpl;
import com.wolf.framework.service.parameter.request.StringRequestParameterHandlerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class RequestParameterHandlerBuilder {

    private final RequestConfig requestConfig;

    public RequestParameterHandlerBuilder(final RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    private ObjectRequestHandlerInfo createObjectHandlerInfo(SecondRequestConfig[] secondRequestConfigs) {
        final List<SecondRequestConfig> requiredRequestConfigList = new ArrayList<>(0);
        final List<SecondRequestConfig> unrequiredRequestConfigList = new ArrayList<>(0);
        for (SecondRequestConfig secondRequestConfig : secondRequestConfigs) {
            if (secondRequestConfig.required()) {
                requiredRequestConfigList.add(secondRequestConfig);
            } else {
                unrequiredRequestConfigList.add(secondRequestConfig);
            }
        }
        RequestParameterHandler requestParameterHandler;
        SecondRequestParameterHandlerBuilder requestParameterHandlerBuilder;
        final Map<String, RequestParameterHandler> requestParameterMap = new HashMap<>(secondRequestConfigs.length, 1);
        List<String> unrequiredNameList = new ArrayList<>(unrequiredRequestConfigList.size());
        for (SecondRequestConfig secondRequestConfig : unrequiredRequestConfigList) {
            requestParameterHandlerBuilder = new SecondRequestParameterHandlerBuilder(secondRequestConfig);
            requestParameterHandler = requestParameterHandlerBuilder.build();
            if (requestParameterHandler != null) {
                requestParameterMap.put(secondRequestConfig.name(), requestParameterHandler);
                unrequiredNameList.add(secondRequestConfig.name());
            }
        }
        final String[] unrequiredNames = unrequiredNameList.toArray(new String[unrequiredNameList.size()]);
        //
        List<String> requiredNameList = new ArrayList<>(requiredRequestConfigList.size());
        for (SecondRequestConfig secondRequestConfig : requiredRequestConfigList) {
            requestParameterHandlerBuilder = new SecondRequestParameterHandlerBuilder(secondRequestConfig);
            requestParameterHandler = requestParameterHandlerBuilder.build();
            if (requestParameterHandler != null) {
                requestParameterMap.put(secondRequestConfig.name(), requestParameterHandler);
                requiredNameList.add(secondRequestConfig.name());
            }
        }
        final String[] requiredNames = requiredNameList.toArray(new String[requiredNameList.size()]);
        //
        ObjectRequestHandlerInfo objectHandlerInfo = new ObjectRequestHandlerInfo(requiredNames, unrequiredNames, requestParameterMap);
        return objectHandlerInfo;
    }

    public RequestParameterHandler build() {
        RequestParameterHandler parameterHandler = null;
        final String fieldName = this.requestConfig.name();
        //基本数据类型
        RequestDataType dataType = this.requestConfig.dataType();
        long max = this.requestConfig.max();
        long min = this.requestConfig.min();
        boolean ignoreEmpty = this.requestConfig.ignoreEmpty();
        String text = this.requestConfig.text();
        switch (dataType) {
            case STRING:
                parameterHandler = new StringRequestParameterHandlerImpl(fieldName, max, min, ignoreEmpty);
                break;
            case DATE:
                parameterHandler = new DateRequestParameterHandlerImpl(fieldName, ignoreEmpty);
                break;
            case DATE_TIME:
                parameterHandler = new DateTimeRequestParameterHandlerImpl(fieldName, ignoreEmpty);
                break;
            case LONG:
                parameterHandler = new LongRequestParameterHandlerImpl(fieldName, max, min, ignoreEmpty);
                break;
            case DOUBLE:
                parameterHandler = new DoubleRequestParameterHandlerImpl(fieldName, max, min, ignoreEmpty);
                break;
            case BOOLEAN:
                parameterHandler = new BooleanRequestParameterHandlerImpl(fieldName, ignoreEmpty);
                break;
            case ENUM:
                String[] enumValues = text.split(",");
                parameterHandler = new EnumRequestParameterHandlerImpl(fieldName, enumValues, ignoreEmpty);
                break;
            case REGEX:
                parameterHandler = new RegexRequestParameterHandlerImpl(fieldName, text, ignoreEmpty);
                break;
            case CHINA_MOBILE:
                parameterHandler = new ChinaMobileRequestParameterHandlerImpl(fieldName, ignoreEmpty);
                break;
            case EMAIL:
                parameterHandler = new EmailRequestParameterHandlerImpl(fieldName, ignoreEmpty);
                break;
            case LONG_ARRAY:
                parameterHandler = new LongArrayRequestParameterHandlerImpl(fieldName, ignoreEmpty);
                break;
            case STRING_ARRAY:
                parameterHandler = new StringArrayRequestParameterHandlerImpl(fieldName, ignoreEmpty);
                break;
            case OBJECT:
                SecondRequestConfig[] secondRequestConfigs = this.requestConfig.secondRequestConfigs();
                if (secondRequestConfigs.length > 0) {
                    ObjectRequestHandlerInfo objectRequestHandlerInfo = this.createObjectHandlerInfo(secondRequestConfigs);
                    parameterHandler = new ObjectRequestParameterHandlerImpl(fieldName, ignoreEmpty, objectRequestHandlerInfo);
                } else {
                    parameterHandler = null;
                }
                break;
            case OBJECT_ARRAY:
                secondRequestConfigs = this.requestConfig.secondRequestConfigs();
                if (secondRequestConfigs.length > 0) {
                    ObjectRequestHandlerInfo objectRequestHandlerInfo = this.createObjectHandlerInfo(secondRequestConfigs);
                    parameterHandler = new ObjectArrayRequestParameterHandlerImpl(fieldName, ignoreEmpty, objectRequestHandlerInfo);
                } else {
                    parameterHandler = null;
                }
                break;
        }
        return parameterHandler;
    }
}
