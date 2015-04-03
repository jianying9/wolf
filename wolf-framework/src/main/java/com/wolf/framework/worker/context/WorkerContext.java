package com.wolf.framework.worker.context;

import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface WorkerContext {

    public Map<String, String> getParameterMap();

    public String getRoute();

    public String getSessionId();
    
    public void saveNewSession(String sid);
    
    public void removeSession();
}
