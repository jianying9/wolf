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
public class SecondRequestParameterHandlerBuilder {

    private final SecondRequestConfig requestConfig;

    public SecondRequestParameterHandlerBuilder(final SecondRequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    public RequestParameterHandler build() {
        RequestParameterHandler parameterHandler = null;
        final String fieldName = this.requestConfig.name();
        //
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
            case OBJECT_ARRAY:
                parameterHandler = new ObjectArrayRequestParameterHandlerImpl(fieldName, ignoreEmpty);
                break;
            case OBJECT:
                ThirdRequestConfig[] thirdRequestConfigs = this.requestConfig.thirdRequestConfigs();
                final List<ThirdRequestConfig> requiredRequestConfigList = new ArrayList<>(0);
                final List<ThirdRequestConfig> unrequiredRequestConfigList = new ArrayList<>(0);
                for (ThirdRequestConfig thirdRequestConfig : thirdRequestConfigs) {
                    if (thirdRequestConfig.required()) {
                        requiredRequestConfigList.add(thirdRequestConfig);
                    } else {
                        unrequiredRequestConfigList.add(thirdRequestConfig);
                    }
                }
                RequestParameterHandler requestParameterHandler;
                ThirdRequestParameterHandlerBuilder requestParameterHandlerBuilder;
                final Map<String, RequestParameterHandler> requestParameterMap = new HashMap<>(thirdRequestConfigs.length, 1);
                List<String> unrequiredNameList = new ArrayList<>(unrequiredRequestConfigList.size());
                for (ThirdRequestConfig thirdRequestConfig : unrequiredRequestConfigList) {
                    requestParameterHandlerBuilder = new ThirdRequestParameterHandlerBuilder(thirdRequestConfig);
                    requestParameterHandler = requestParameterHandlerBuilder.build();
                    if (requestParameterHandler != null) {
                        requestParameterMap.put(thirdRequestConfig.name(), requestParameterHandler);
                        unrequiredNameList.add(thirdRequestConfig.name());
                    }
                }
                final String[] unrequiredNames = unrequiredNameList.toArray(new String[unrequiredNameList.size()]);
                //
                List<String> requiredNameList = new ArrayList<>(requiredRequestConfigList.size());
                for (ThirdRequestConfig thirdRequestConfig : requiredRequestConfigList) {
                    requestParameterHandlerBuilder = new ThirdRequestParameterHandlerBuilder(thirdRequestConfig);
                    requestParameterHandler = requestParameterHandlerBuilder.build();
                    if (requestParameterHandler != null) {
                        requestParameterMap.put(thirdRequestConfig.name(), requestParameterHandler);
                        requiredNameList.add(thirdRequestConfig.name());
                    }
                }
                final String[] requiredNames = requiredNameList.toArray(new String[requiredNameList.size()]);
                //
                parameterHandler = new ObjectRequestParameterHandlerImpl(fieldName, ignoreEmpty, requiredNames, unrequiredNames, requestParameterMap);
                break;
        }
        return parameterHandler;
    }
}
