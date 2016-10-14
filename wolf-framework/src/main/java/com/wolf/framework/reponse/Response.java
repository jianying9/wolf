package com.wolf.framework.reponse;

/**
 *
 * @author jianying9
 */
public interface Response {
    
    public String getState();
    
    public void setState(String state);
    
    public void denied();

    public void invalid();

    public void unlogin();

    public String getResponseMessage();

    public String getResponseMessage(boolean useCache);
}
