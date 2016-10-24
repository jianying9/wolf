package com.wolf.framework.request;

import java.util.Map;

/**
 *  普通类别请求
 * @author jianying9
 */
public interface Request {

    public Map<String, String> getParameterMap();
    
    public String getParameter(String name);
    
    public String getRoute();

    public String getSessionId();
}
