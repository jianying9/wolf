package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.utils.EntityUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ObjectResponseImpl<T extends Entity> extends AbstractResponse implements ObjectResponse<T> {

    private final String[] returnParameter;
    private final Map<String, ResponseParameterHandler> parameterHandlerMap;
    private Map<String, Object> dataMap = null;

    public ObjectResponseImpl(Response response, String[] returnParameter, Map<String, ResponseParameterHandler> responseHandlerMap) {
        super(response);
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = responseHandlerMap;
    }

    @Override
    public void setEntity(T t) {
        Map<String, Object> tMap = EntityUtils.getMap(t);
        this.setDataMap(tMap);
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    @Override
    public String getDataMessage() {
        String dataMessage = null;
        //检查并过滤响应参数
        if (this.dataMap != null) {
            Object paraValue;
            ResponseParameterHandler responseParameterHandler;
            for (String paraName : this.returnParameter) {
                paraValue = this.dataMap.get(paraName);
                if (paraValue != null) {
                    responseParameterHandler = this.parameterHandlerMap.get(paraName);
                    paraValue = responseParameterHandler.getResponseValue(paraValue);
                    this.dataMap.put(paraName, paraValue);
                }
            }
        }
        //输出json
        ObjectMapper mapper = new ObjectMapper();
        try {
            dataMessage = mapper.writeValueAsString(this.dataMap);
        } catch (IOException ex) {
        }
        return dataMessage;
    }

    @Override
    public void setData(String name, Object value) {
        Map<String, Object> newDataMap = new HashMap<>(2, 1);
        newDataMap.put(name, value);
        this.dataMap = newDataMap;
    }

}
