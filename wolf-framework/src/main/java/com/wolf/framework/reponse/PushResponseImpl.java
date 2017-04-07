package com.wolf.framework.reponse;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.service.parameter.PushHandler;
import com.wolf.framework.utils.EntityUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import com.wolf.framework.service.parameter.ResponseHandler;
import java.util.Collections;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class PushResponseImpl<T extends Entity> implements PushResponse<T> {

    private final PushHandler pushHandler;
    private Map<String, Object> dataMap = Collections.EMPTY_MAP;
    private String pushId = null;

    public PushResponseImpl(PushHandler pushHandler) {
        this.pushHandler = pushHandler;
    }
    
    @Override
    public String getPushId() {
        return this.pushId;
    }
    
    @Override
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    protected final Map<String, Object> checkAndFilterDataMap(Map<String, Object> paraMap) {
        Map<String, Object> resultMap = null;
        if (paraMap != null) {
            String[] returnParameter = this.pushHandler.getReturnParameter();
            Map<String, ResponseHandler> parameterHandlerMap = this.pushHandler.getResponseHandlerMap();
            Object paraValue;
            ResponseHandler responseParameterHandler;
            resultMap = new HashMap(paraMap.size(), 1);
            //过滤
            for (String paraName : returnParameter) {
                paraValue = paraMap.get(paraName);
                if (paraValue != null) {
                    responseParameterHandler = parameterHandlerMap.get(paraName);
                    paraValue = responseParameterHandler.getResponseValue(paraValue);
                    resultMap.put(paraName, paraValue);
                }
            }
        }
        return resultMap;
    }

    @Override
    public void setEntity(T t) {
        Map<String, Object> tMap = EntityUtils.getMap(t);
        this.setDataMap(tMap);
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = this.checkAndFilterDataMap(dataMap);
    }

    @Override
    public void setData(String name, Object value) {
        Map<String, Object> newDataMap = new HashMap<>(2, 1);
        newDataMap.put(name, value);
        //检测并过滤响应参数
        this.dataMap = this.checkAndFilterDataMap(newDataMap);
    }

    @Override
    public String getPushMessage() {
        String responseMsg = "{}";
        Map<String, Object> responseMap = new HashMap(8, 1);
        //核心返回数据
        responseMap.put("route", this.pushHandler.getRoute());
        responseMap.put("data", this.dataMap);
        responseMap.put("pushId", this.pushId);
        //
        ObjectMapper mapper = new ObjectMapper();
        try {
            responseMsg = mapper.writeValueAsString(responseMap);
        } catch (IOException ex) {
        }
        return responseMsg;
    }
    
}
