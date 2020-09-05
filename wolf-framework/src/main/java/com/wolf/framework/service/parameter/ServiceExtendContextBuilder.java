package com.wolf.framework.service.parameter;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

/**
 * 负责解析annotation ServiceExtendConfig
 *
 * @author jianying9
 */
public class ServiceExtendContextBuilder {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    private final Map<String, List<RequestInfo>> requestExtendMap = new HashMap(2, 1);
    private final Map<String, List<ResponseInfo>> responseExtendMap = new HashMap(2, 1);

    /**
     * 解析方法
     *
     * @param clazz
     */
    public void add(final Class<?> clazz) {
        this.logger.debug("--parsing service extend {}--", clazz.getName());
        if (clazz.isAnnotationPresent(ServiceExtendConfig.class)) {
            //
            Field[] fieldTemp = clazz.getDeclaredFields();
            String fieldValue;
            RequestGroupConfig requestGroupConfig;
            ResponseGroupConfig responseGroupConfig;
            List<RequestInfo> requestInfoList;
            List<ResponseInfo> responsInfoList;
            RequestInfo requestInfo;
            ResponseInfo responseInfo;
            try {
                for (Field field : fieldTemp) {
                fieldValue = String.valueOf(field.get(clazz));
                if (field.isAnnotationPresent(RequestGroupConfig.class)) {
                    //请求集合组
                    requestGroupConfig = field.getAnnotation(RequestGroupConfig.class);
                    requestInfoList = new ArrayList(requestGroupConfig.requestConfigs().length);
                    for (ExtendRequestConfig extendRequestConfig : requestGroupConfig.requestConfigs()) {
                        if(extendRequestConfig.dataType() != RequestDataType.EXTEND) {
                            requestInfo = new RequestInfoImpl(extendRequestConfig);
                            requestInfoList.add(requestInfo);
                        }
                    }
                    if (this.requestExtendMap.containsKey(fieldValue)) {
                        StringBuilder errBuilder = new StringBuilder(1024);
                        errBuilder.append("Error service extend config. Cause: fieldName reduplicated : ").append(fieldValue).append("\n").append("exist class : ").append(clazz);
                        throw new RuntimeException(errBuilder.toString());
                    }
                    this.requestExtendMap.put(fieldValue, requestInfoList);
                }
                if (field.isAnnotationPresent(ResponseGroupConfig.class)) {
                    //响应集合组
                    responseGroupConfig = field.getAnnotation(ResponseGroupConfig.class);
                    responsInfoList = new ArrayList(responseGroupConfig.responseConfigs().length);
                    for (ExtendResponseConfig extendResponseConfig : responseGroupConfig.responseConfigs()) {
                        if(extendResponseConfig.dataType() != ResponseDataType.EXTEND) {
                            responseInfo = new ResponseInfoImpl(extendResponseConfig);
                            responsInfoList.add(responseInfo);
                        }
                    }
                    if (this.responseExtendMap.containsKey(fieldValue)) {
                        StringBuilder errBuilder = new StringBuilder(1024);
                        errBuilder.append("Error service extend config. Cause: fieldName reduplicated : ").append(fieldValue).append("\n").append("exist class : ").append(clazz);
                        throw new RuntimeException(errBuilder.toString());
                    }
                    this.responseExtendMap.put(fieldValue, responsInfoList);
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
    
    public ServiceExtendContext build() {
        ServiceExtendContext serviceExtend = new ServiceExtendContext(this.requestExtendMap, this.responseExtendMap);
        return serviceExtend;
    }
}
