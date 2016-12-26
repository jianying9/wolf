package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;

/**
 *
 * @author jianying9
 * @param <T>
 */
public abstract class AbstractResponse<T extends Entity>  implements BaseResponse {

    protected final Response response;
    
    
    public AbstractResponse(Response response) {
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
}
