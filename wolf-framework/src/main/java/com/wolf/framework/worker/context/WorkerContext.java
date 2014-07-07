package com.wolf.framework.worker.context;

import com.wolf.framework.session.Session;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface WorkerContext {

    public Map<String, String> getParameterMap();

    public String getAct();

    public void sendMessage(String message);

    public void close();

    public void saveNewSession(Session newSession);
    
    public void removeSession();

    public Session getSession();
}
