package com.wolf.framework.worker.context;

import com.wolf.framework.config.ResponseFlagType;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.session.Session;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface MessageContext {

    public String getParameter(String name);

    public Map<String, String> getParameterMap();

    public int getPageIndex();

    public int getPageSize();

    public void setPageTotal(int pageTotal);
    
    public void setPageNum(int pageNum);
    
    public Session getSession();
    
    public void setNewSession(Session session);
    
    public boolean isOnline(String userId);
    
    public void setMapData(Map<String, String> parameterMap);

    public void setMapListData(List<Map<String, String>> parameterMapList);

    public <T extends Entity> void setEntityData(T t);

    public <T extends Entity> void setEntityListData(List<T> tList);

    public void success();
    
    public void setFlag(ResponseFlagType responseFlagType);

    public void addBroadcastUserIdList(List<String> broadcastUserIdList);

    public void addBroadcastUserId(String broadcastUserId);

    public ApplicationContext getApplicationContext();
}
