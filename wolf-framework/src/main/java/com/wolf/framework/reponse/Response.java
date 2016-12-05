package com.wolf.framework.reponse;

/**
 *
 * @author jianying9
 */
public interface Response {
    
    public String getCode();
    
    public void setCode(String code);
    
    public String getDataMessage();
    
    public void setDataMessage(String dataMessage);
    
    public void success();
    
    public void exception();
    
    public void unsupport();
    
    public void unlogin();
    
    public void setNewSessionId(String sid);
    
    public String getNewSessionId();
    
    public void setPushId(String pushId);
    
    public String getPushId();
    
    public String getResponseMessage();
    
    public String getPushMessage();
    
    public void closeOtherSession(String otherSid);
}
