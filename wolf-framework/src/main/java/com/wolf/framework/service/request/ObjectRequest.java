package com.wolf.framework.service.request;

import java.util.List;
import java.util.Map;

/**
 *  普通类别请求
 * @author jianying9
 */
public interface ObjectRequest {

    public Map<String, Object> getValueMap();
    
    public Object getValue(String name);
    
    public long getLongValue(String name);
    
    public boolean getBooleanValue(String name);
    
    public double getDoubleValue(String name);
    
    public String getStringValue(String name);
    
    public List<Long> getLongListValue(String name);
    
    public List<String> getStringListValue(String name);
    
    public String getRoute();

    public String getSessionId();
}
