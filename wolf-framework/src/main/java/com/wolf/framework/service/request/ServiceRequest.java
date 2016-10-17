package com.wolf.framework.service.request;

import java.util.Map;

/**
 *  普通类别请求
 * @author jianying9
 */
public interface ServiceRequest {

    public Map<String, String> getParameterMap();
    
    public String getParameter(String name);
    
    public String getRoute();

    public String getSessionId();
    
    public void setNewSessionId(String sid);
    
    public String getNewSessionId();
    
    public boolean push(String sid, String responseMessage);
}
