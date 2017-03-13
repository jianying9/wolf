package com.wolf.framework.service.parameter.response;

import com.wolf.framework.service.parameter.ObjectResponseHandlerInfo;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object array类型处理类
 *
 * @author aladdin
 */
public final class ObjectArrayResponseParameterHandlerImpl implements ResponseParameterHandler {

    private final String name;
    private final String[] parameter;
    private final Map<String, ResponseParameterHandler> responseHandlerMap;

    public ObjectArrayResponseParameterHandlerImpl(String name, ObjectResponseHandlerInfo objectRequestHandlerInfo) {
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
        return ResponseDataType.OBJECT_ARRAY;
    }

    @Override
    public Object getResponseValue(Object value) {
        List<Object> resultObjectList = null;
        boolean isObjectArray = true;
        if (List.class.isInstance(value) == false) {
            isObjectArray = false;
        } else {
            List<Object> objectList = (List<Object>) value;
            resultObjectList = new ArrayList(objectList.size());
            Map<String, Object> mapObj;
            Map<String, Object> resultObj;
            Object paraValue;
            ResponseParameterHandler responseParameterHandler;
            for (Object obj : objectList) {
                if (Map.class.isInstance(obj) == false) {
                    isObjectArray = false;
                    break;
                } else {
                    mapObj = (Map<String, Object>) obj;
                    resultObj = new HashMap(mapObj.size(), 1);
                    //过滤
                    for (String paraName : this.parameter) {
                        paraValue = mapObj.get(paraName);
                        if (paraValue != null) {
                            responseParameterHandler = this.responseHandlerMap.get(paraName);
                            paraValue = responseParameterHandler.getResponseValue(paraValue);
                            resultObj.put(paraName, paraValue);
                        }
                    }
                    //
                    resultObjectList.add(resultObj);
                }
            }
        }
        if (isObjectArray == false) {
            String errMsg = "response:" + this.name + "'s type is not Object array.";
            throw new RuntimeException(errMsg);
        }
        return resultObjectList;
    }
}
