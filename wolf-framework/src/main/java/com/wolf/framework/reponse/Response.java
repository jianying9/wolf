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
    
    public void setNewSessionId(String sid);
    
    public String getNewSessionId();
    
    public String getResponseMessage();
    
    public String getResponseMessage(boolean useCache);
    
    public boolean push(String sid, String responseMessage);
}
