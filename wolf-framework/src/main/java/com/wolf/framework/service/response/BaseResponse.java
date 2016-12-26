package com.wolf.framework.service.response;

/**
 *
 * @author jianying9
 */
public interface BaseResponse {
    
    public String getCode();
    
    public void setCode(String code);
    
    public void success();
    
    public void unlogin();
    
    public String getDataMessage();
    
    public void setNewSessionId(String sid);
    
    public String getNewSessionId();
    
    public void setPushId(String pushId);
    
    public String getPushId();
    
    public String getPushMessage();
    
    public void closeOtherSession(String otherSid);
}
