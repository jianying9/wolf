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

    public final String getState() {
        return this.response.getState();
    }
    
    public final void setState(String state) {
        this.response.setState(state);
    }

    public final void success() {
        this.response.success();
    }
    
    public final void failure() {
        this.response.failure();
    }
}
