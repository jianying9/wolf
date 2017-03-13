package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public abstract class AbstractResponse<T extends Entity>  implements BaseResponse {

    protected final Response response;
    private final String[] returnParameter;
    private final Map<String, ResponseParameterHandler> parameterHandlerMap;
    
    
    public AbstractResponse(Response response, String[] returnParameter, Map<String, ResponseParameterHandler> responseHandlerMap) {
        this.response = response;
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = responseHandlerMap;
    }
    
    @Override
    public final String getCode() {
        return this.response.getCode();
    }
    
    @Override
    public final void setCode(String code) {
        this.response.setCode(code);
    }

    @Override
    public final void success() {
        this.response.success();
    }
    
    @Override
    public final void unlogin() {
        this.response.unlogin();
    }
    
    @Override
    public String getNewSessionId() {
        return this.response.getNewSessionId();
    }

    @Override
    public final void setNewSessionId(String newSessionId) {
        this.response.setNewSessionId(newSessionId);
    }
    
    @Override
    public abstract String getDataMessage();
    
    @Override
    public String getPushMessage() {
        String dataMessage = this.getDataMessage();
        this.response.setDataMessage(dataMessage);
        return this.response.getPushMessage();
    }
    
    @Override
    public void closeOtherSession(String otherSid) {
        this.response.closeOtherSession(otherSid);
    }
    
    @Override
    public void setPushId(String pushId) {
        this.response.setPushId(pushId);
    }

    @Override
    public String getPushId() {
        return this.response.getPushId();
    }
    
    protected final Map<String, Object> checkAndFilterDataMap(Map<String, Object> paraMap) {
        Map<String, Object> resultMap = null;
        if (paraMap != null) {
            Object paraValue;
            ResponseParameterHandler responseParameterHandler;
            resultMap = new HashMap(paraMap.size(), 1);
            //过滤
            for (String paraName : this.returnParameter) {
                paraValue = paraMap.get(paraName);
                if (paraValue != null) {
                    responseParameterHandler = this.parameterHandlerMap.get(paraName);
                    paraValue = responseParameterHandler.getResponseValue(paraValue);
                    resultMap.put(paraName, paraValue);
                }
            }
        }
        return resultMap;
    }
    
}
