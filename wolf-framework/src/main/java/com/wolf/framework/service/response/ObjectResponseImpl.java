package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.utils.JsonUtils;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ObjectResponseImpl<T extends Entity> extends AbstractServiceResponse implements ObjectResponse<T> {
    
    private final String[] returnParameter;
    private final Map<String, ResponseParameterHandler> parameterHandlerMap;
    private Map<String, String> dataMap = null;

    public ObjectResponseImpl(Response response, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap) {
        super(response);
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = parameterHandlerMap;
    }

    @Override
    public void setEntity(Entity t) {
        this.setDataMap(t.toMap());
    }

    @Override
    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }

    @Override
    public String getDataMessage() {
        String objectMessage;
        if(this.dataMap == null) {
            objectMessage = "null";
        } else {
            objectMessage = JsonUtils.mapToJSON(this.dataMap, this.returnParameter, this.parameterHandlerMap);
        }
        StringBuilder jsonBuilder = new StringBuilder(objectMessage.length() + 11);
        jsonBuilder.append("{\"object\":").append(objectMessage).append("}");
        String dataMessage = jsonBuilder.toString();
        return dataMessage;
    }

    @Override
    public void setData(String name, String value) {
        Map<String, String> newDataMap = new HashMap<>(2, 1);
        newDataMap.put(name, value);
        this.dataMap = newDataMap;
    }

}
