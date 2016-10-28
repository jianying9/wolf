package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface BaseServiceResponse<T extends Entity> {
    
    public String getCode();
    
    public void setCode(String code);
    
    public void success();
    
    public String getDataMessage();
    
    public void setNewSessionId(String sid);
    
    public String getNewSessionId();
    
    public String getResponseMessage();

    public boolean push(String sid);
    
    public boolean push(String sid, String responseMessage);
    
    public boolean asyncPush(String sid);
    
    public boolean asyncPush(String sid, String responseMessage);
    
    public void closeOtherSession(String otherSid);
}
