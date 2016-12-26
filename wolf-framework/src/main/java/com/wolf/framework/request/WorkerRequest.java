package com.wolf.framework.request;

/**
 *  普通类别请求
 * @author jianying9
 */
public interface WorkerRequest extends Request {

    
    public void putParameter(String name , Object value);
    
    public void removeSession();
}
