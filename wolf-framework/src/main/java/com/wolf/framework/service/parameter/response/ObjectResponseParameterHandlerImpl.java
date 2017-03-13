package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.ObjectResponseHandlerInfo;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        Map<String, Object> resultMap = null;
        if(Map.class.isInstance(value) == false) {
            String errMsg = "response:" + this.name + "'s type is not Object.";
            throw new RuntimeException(errMsg);
        } else {
            Map<String, Object> valueMap = (Map<String, Object>) value;
            resultMap = new HashMap(valueMap.size(), 1);
            Object paraValue;
            ResponseParameterHandler responseParameterHandler;
            //过滤
            for (String paraName : this.parameter) {
                paraValue = valueMap.get(paraName);
                if(paraValue != null) {
                    responseParameterHandler = this.responseHandlerMap.get(paraName);
                    paraValue = responseParameterHandler.getResponseValue(paraValue);
                    resultMap.put(paraName, paraValue);
                }
            }
        }
        return resultMap;
    }
}
