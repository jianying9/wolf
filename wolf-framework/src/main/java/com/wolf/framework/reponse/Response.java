package com.wolf.framework.reponse;

import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface Response<T extends Entity> {
    
    public String getCode();
    
    public void setCode(String code);
    
    public void success();
    
    public void exception();
    
    public void unsupport();
    
    public void unlogin();
    
    public void setNewSessionId(String sid);
    
    public String getNewSessionId();
    
    public String getResponseMessage();
    
    public void closeOtherSession(String otherSid);
    
    public void setDataMap(Map<String, Object> dataMap);
    
    public Map<String, Object> getDataMap();
    
    public void setData(String name, Object value);
    
    public void setResponseText(String text);
    
    public void setEntity(T t);
    
    public PushResponse getPushResponse(String route);
    
}
