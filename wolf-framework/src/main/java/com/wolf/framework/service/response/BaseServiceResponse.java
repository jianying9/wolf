package com.wolf.framework.service.response;

/**
 *
 * @author jianying9
 */
public interface BaseServiceResponse {
    
    public String getCode();
    
    public void setCode(String code);
    
    public void success();
    
    public void unlogin();
    
    public String getDataMessage();
    
    public void setNewSessionId(String sid);
    
    public String getNewSessionId();
    
    public String getPushMessage();
    
    public boolean push(String sid);
    
    public boolean push(String sid, String responseMessage);
    
    public boolean asyncPush(String sid);
    
    public boolean asyncPush(String sid, String responseMessage);
    
    public void closeOtherSession(String otherSid);
}
