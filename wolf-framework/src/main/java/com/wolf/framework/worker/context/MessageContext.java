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
public interface MessageContext {

    public String getParameter(String name);

    public Map<String, String> getParameterMap();

    public long getPageIndex();

    public long getPageSize();

    public void setPageTotal(long pageTotal);

    public void setPageNum(long pageNum);

    public Session getSession();

    public void setNewSession(Session session);

    public boolean isOnline(String userId);

    public void setMapData(Map<String, String> parameterMap);

    public void setMapListData(List<Map<String, String>> parameterMapList);

    public <T extends Entity> void setEntityData(T t);

    public <T extends Entity> void setEntityListData(List<T> tList);

    public void success();

    public void setFlag(String flag);

    public void addBroadcastUserIdList(List<String> broadcastUserIdList);

    public void addBroadcastUserId(String broadcastUserId);

    public ApplicationContext getApplicationContext();
}
