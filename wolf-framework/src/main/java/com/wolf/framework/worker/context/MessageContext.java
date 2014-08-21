package com.wolf.framework.worker.context;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.session.Session;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface MessageContext extends Response {

    public String getParameter(String name);

    public Map<String, String> getParameterMap();

    public void setPageTotal(long pageTotal);

    @Deprecated
    public Session getSession();

    @Deprecated
    public void setNewSession(Session session);
    
    public void setNewSessionId(String sid);

    public void setMapData(Map<String, String> parameterMap);

    public void setMapListData(List<Map<String, String>> parameterMapList);

    public <T extends Entity> void setEntityData(T t);

    public <T extends Entity> void setEntityListData(List<T> tList);

    public void success();

    public void setState(String state);

    public ApplicationContext getApplicationContext();

    public boolean push(String sid, String responseMessage);
}
