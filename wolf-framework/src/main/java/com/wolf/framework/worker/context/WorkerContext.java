package com.wolf.framework.worker.context;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.WorkerRequest;
import com.wolf.framework.worker.ServiceWorker;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface WorkerContext {

    public Map<String, Object> getParameterMap();
    
    public Object getParameter(String name);
    
    public String getCallback();
    
    public String getMd5();
    
    public boolean isPretty();
    
    public String getRoute();

    public String getSessionId();
    
    public void saveNewSession(String sid);
    
    public void removeSession();
    
    public void closeSession(String sid);
    
    public WorkerRequest getWorkerRequest();
    
    public WorkerResponse getWorkerResponse();
    
    public ServiceWorker getServiceWorker();
    
    public ApplicationContext getApplicationContext();
}
