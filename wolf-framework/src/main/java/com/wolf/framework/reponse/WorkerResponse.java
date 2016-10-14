package com.wolf.framework.reponse;

/**
 *
 * @author jianying9
 */
public interface WorkerResponse extends Response {
    
    public void setError(String error);

    public String createErrorMessage();
}
