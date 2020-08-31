package com.wolf.framework.service.parameter;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

/**
 * 负责解析annotation ServicePushConfig
 *
 * @author jianying9
 */
public class ServicePushContextBuilder {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    private final Map<String, PushInfo> pushInfoMap = new HashMap(2, 1);
    private final Map<String, PushHandler> pushHandlerMap = new HashMap(2, 1);
    private final ServiceExtendContext serviceExtendContext;

    public ServicePushContextBuilder(ServiceExtendContext serviceExtendContext) {
        this.serviceExtendContext = serviceExtendContext;
    }
    
    private List<ResponseInfo> executeResponseExtend(List<ResponseInfo> responseInfoList) {
        List<ResponseInfo> resultList = Collections.EMPTY_LIST;
        if (responseInfoList.isEmpty() == false) {
            Map<String, ResponseInfo> responseInfoMap = new HashMap();
            List<ResponseInfo> extendRequestInfoList;
            List<ResponseInfo> childList;
            for (ResponseInfo responseInfo : responseInfoList) {
                if (responseInfo.getDataType() == ResponseDataType.EXTEND) {
                    //外部参数
                    extendRequestInfoList = this.serviceExtendContext.getResponseExtend(responseInfo.getName());
                    if (extendRequestInfoList != null) {
                        for (ResponseInfo extendResponseInfo : extendRequestInfoList) {
                            responseInfoMap.put(extendResponseInfo.getName(), extendResponseInfo);
                        }
                    }
                } else {
                    //过滤子参数
                    childList = this.executeResponseExtend(responseInfo.getChildList());
                    responseInfo.setChildList(childList);
                    responseInfoMap.put(responseInfo.getName(), responseInfo);
                }
            }
            resultList = new ArrayList(responseInfoMap.size());
            resultList.addAll(responseInfoMap.values());
        }
        return resultList;
    }

    /**
     * 解析方法
     *
     * @param clazz
     */
    public void add(final Class<?> clazz) {
        this.logger.debug("--parsing service push {}--", clazz.getName());
        if (clazz.isAnnotationPresent(ServicePushConfig.class)) {
            //
            Field[] fieldTemp = clazz.getDeclaredFields();
            String fieldValue;
            PushConfig pushConfig;
            PushInfo pushInfo;
            PushHandler pushHandler;
            List<ResponseInfo> pushResponseInfoResultList;
            Map<String, ResponseHandler> pushResponseHandlerMap;
            String[] pushReturnNames;
            List<String> pushReturnNameList;
            List<ResponseInfo> pushResponseInfoList;
            ResponseHandler responseHandler;
            ResponseHandlerBuilder responseHandlerBuilder;
            String pushRouteName;
            try {
                for (Field field : fieldTemp) {
                    if (field.isAnnotationPresent(PushConfig.class)) {
                        //请求集合组
                        fieldValue = String.valueOf(field.get(clazz));
                        pushConfig = field.getAnnotation(PushConfig.class);
                        pushInfo = new PushInfoImpl(fieldValue, pushConfig);
                        if (this.pushInfoMap.containsKey(fieldValue)) {
                            StringBuilder errBuilder = new StringBuilder(1024);
                            errBuilder.append("Error service push config. Cause: fieldName reduplicated : ").append(fieldValue).append("\n").append("exist class : ").append(clazz);
                            throw new RuntimeException(errBuilder.toString());
                        }
                        //处理外部请求参数
                        pushResponseInfoResultList = this.executeResponseExtend(pushInfo.getResponseInfoList());
                        pushInfo.setResponseInfoList(pushResponseInfoResultList);
                        this.pushInfoMap.put(fieldValue, pushInfo);
                        //
                        pushRouteName = pushInfo.getRoute();
                        pushResponseInfoList = pushInfo.getResponseInfoList();
                        pushReturnNameList = new ArrayList(pushResponseInfoList.size());
                        pushResponseHandlerMap = new HashMap(pushResponseInfoList.size(), 1);
                        for (ResponseInfo responseInfo : pushResponseInfoList) {
                            responseHandlerBuilder = new ResponseHandlerBuilder(
                                    responseInfo);
                            responseHandler = responseHandlerBuilder.build();
                            if (responseHandler != null) {
                                pushResponseHandlerMap.put(responseInfo.getName(), responseHandler);
                                pushReturnNameList.add(responseInfo.getName());
                            }
                        }
                        pushReturnNames = pushReturnNameList.toArray(new String[pushReturnNameList.size()]);
                        //
                        pushHandler = new PushHandler(pushRouteName, pushReturnNames, pushResponseHandlerMap);
                        pushHandlerMap.put(pushRouteName, pushHandler);
                    }
                }
            } catch (IllegalAccessException | RuntimeException e) {
                throw new RuntimeException(e);
            }
            this.logger.debug("--parse service extend {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse service extend {} missing annotation ServiceExtendConfig--", clazz.getName());
        }
    }

    public ServicePushContext build() {
        ServicePushContext servicePushContext = new ServicePushContext(this.pushInfoMap, this.pushHandlerMap);
        return servicePushContext;
    }
}
