package com.wolf.framework.service.response;

import com.wolf.framework.reponse.Response;

/**
 *
 * @author jianying9
 */
public abstract class AbstractServiceResponse  {

    protected final Response response;
    
    public AbstractServiceResponse(Response response) {
        this.response = response;
    }

    public final String getCode() {
        return this.response.getCode();
    }
    
    public final void setCode(String code) {
        this.response.setCode(code);
    }

    public final void success() {
        this.response.success();
    }
    
    public String getNewSessionId() {
        return this.response.getNewSessionId();
    }

    public final void setNewSessionId(String newSessionId) {
        this.response.setNewSessionId(newSessionId);
    }
    
    public abstract String getDataMessage();
    
    public String getResponseMessage() {
        String dataMessage = this.getDataMessage();
        this.response.setDataMessage(dataMessage);
        return this.response.getResponseMessage();
    }
    
    public boolean push(String sid) {
        String responseMessage = this.getResponseMessage();
        return this.push(sid, responseMessage);
    }
    
    public boolean push(String sid, String responseMessage) {
        return this.response.push(sid, responseMessage);
    }
    
    public boolean asyncPush(String sid) {
        String responseMessage = this.getResponseMessage();
        return this.asyncPush(sid, responseMessage);
    }
    
    public boolean asyncPush(String sid, String responseMessage) {
        return this.response.asyncPush(sid, responseMessage);
    }
    
    public void closeOtherSession(String otherSid) {
        this.response.closeOtherSession(otherSid);
    }
}
