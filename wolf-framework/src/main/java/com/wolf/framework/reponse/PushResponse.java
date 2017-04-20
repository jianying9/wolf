package com.wolf.framework.reponse;

import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface PushResponse<T extends Entity> {
    
    public void setDataMap(Map<String, Object> dataMap);
    
    public void setData(String name, Object value);
    
    public void setEntity(T t);
    
    public String getPushId();
    
    public void setPushId(String pushId);
    
    public String getPushMessage();
}
