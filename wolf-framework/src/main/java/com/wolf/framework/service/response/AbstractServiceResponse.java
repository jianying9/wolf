package com.wolf.framework.service.response;

import com.wolf.framework.reponse.Response;

/**
 *
 * @author jianying9
 */
public abstract class AbstractServiceResponse  implements BaseServiceResponse {

    protected final Response response;
    
    public AbstractServiceResponse(Response response) {
        this.response = response;
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
    public String getResponseMessage() {
        String dataMessage = this.getDataMessage();
        this.response.setDataMessage(dataMessage);
        return this.response.getResponseMessage();
    }
    
    @Override
    public boolean push(String sid) {
        String responseMessage = this.getResponseMessage();
        return this.push(sid, responseMessage);
    }
    
    @Override
    public boolean push(String sid, String responseMessage) {
        return this.response.push(sid, responseMessage);
    }
    
    @Override
    public boolean asyncPush(String sid) {
        String responseMessage = this.getResponseMessage();
        return this.asyncPush(sid, responseMessage);
    }
    
    @Override
    public boolean asyncPush(String sid, String responseMessage) {
        return this.response.asyncPush(sid, responseMessage);
    }
    
    @Override
    public void closeOtherSession(String otherSid) {
        this.response.closeOtherSession(otherSid);
    }
}
