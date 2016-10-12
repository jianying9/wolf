package com.wolf.framework.worker.context;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface MessageContext extends Response {

    public String getParameter(String name);

    public Map<String, String> getParameterMap();
    
    public void setNewSessionId(String sid);

    public void setMapData(Map<String, String> parameterMap);

    public <T extends Entity> void setEntityData(T t);

    public void success();

    public void setState(String state);

    public ApplicationContext getApplicationContext();

    public boolean push(String sid, String responseMessage);
}
