package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.ObjectResponseHandlerInfo;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import java.util.Map;

/**
 * Object类型处理类
 *
 * @author aladdin
 */
public final class ObjectResponseParameterHandlerImpl implements ResponseParameterHandler {

    private final String name;
    private final String[] parameter;
    private final Map<String, ResponseParameterHandler> responseHandlerMap;

    public ObjectResponseParameterHandlerImpl(String name, ObjectResponseHandlerInfo objectRequestHandlerInfo) {
        this.name = name;
        this.parameter = objectRequestHandlerInfo.getParameter();
        this.responseHandlerMap = objectRequestHandlerInfo.getResponseParameterHandlerMap();
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResponseDataType getDataType() {
        return ResponseDataType.OBJECT;
    }

    @Override
    public Object getResponseValue(Object value) {
        if(Map.class.isInstance(value) == false) {
            String errMsg = "response:" + this.name + "'s type is not Object.";
            throw new RuntimeException(errMsg);
        } else {
            Map<String, Object> valueMap = (Map<String, Object>) value;
            Object paraValue;
            ResponseParameterHandler responseParameterHandler;
            for (String paraName : this.parameter) {
                paraValue = valueMap.get(paraName);
                if(paraValue != null) {
                    responseParameterHandler = this.responseHandlerMap.get(paraName);
                    paraValue = responseParameterHandler.getResponseValue(paraValue);
                    valueMap.put(paraName, paraValue);
                }
            }
        }
        return value;
    }
}
