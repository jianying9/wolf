package com.wolf.framework.reponse;

/**
 *
 * @author jianying9
 */
public interface Response {
    
    public String getState();
    
    public void setState(String state);
    
    public String getDataMessage();
    
    public void setDataMessage(String dataMessage);
    
    public void success();
    
    public void failure();
    
    public String getResponseMessage();

    public String getResponseMessage(boolean useCache);
}
