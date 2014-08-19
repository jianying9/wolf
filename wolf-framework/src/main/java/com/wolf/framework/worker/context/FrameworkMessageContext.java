package com.wolf.framework.worker.context;

/**
 *
 * @author aladdin
 */
public interface FrameworkMessageContext extends MessageContext {
    
    public WorkerContext getWorkerContext();
    
    public void putParameter(String name, String value);
    
    public void denied();

    public void invalid();

    public void unlogin();

    public void setError(String error);

    public String createErrorMessage();
    
    public String getNewSessionId();
}
