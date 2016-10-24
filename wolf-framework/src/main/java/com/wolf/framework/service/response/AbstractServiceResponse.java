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
    
    public final void failure() {
        this.response.failure();
    }
    
    public String getNewSessionId() {
        return this.response.getNewSessionId();
    }

    public final void setNewSessionId(String newSessionId) {
        this.response.setNewSessionId(newSessionId);
    }
}
