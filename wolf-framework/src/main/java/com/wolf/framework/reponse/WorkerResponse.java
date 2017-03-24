package com.wolf.framework.reponse;

import com.wolf.framework.dao.Entity;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface WorkerResponse<T extends Entity> extends Response<T> {
    
    public void denied();

    public void invalid();

    public void timeout();
    
    public void setError(String error);
}
