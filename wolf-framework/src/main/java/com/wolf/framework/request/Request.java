package com.wolf.framework.request;

import java.util.Map;

/**
 *  普通类别请求
 * @author jianying9
 */
public interface Request {

    public Map<String, Object> getParameterMap();
    
    public Object getParameter(String name);
    
    public String getRoute();

    public String getSessionId();
}
