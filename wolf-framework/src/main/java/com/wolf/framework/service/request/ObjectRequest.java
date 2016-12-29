package com.wolf.framework.service.request;

import java.util.List;
import java.util.Map;

/**
 *  普通类别请求
 * @author jianying9
 */
public interface ObjectRequest {

    public Map<String, Object> getValueMap();
    
    public Long getLongValue(String name);
    
    public Boolean getBooleanValue(String name);
    
    public Double getDoubleValue(String name);
    
    public String getStringValue(String name);
    
    public List<Long> getLongListValue(String name);
    
    public List<String> getStringListValue(String name);
    
    public Map<String, Object> getObjectValue(String name);
    
    public List<Map<String, Object>> getObjectListValue(String name);
    
    public Long getLongValue(Map<String, Object> object, String name);
    
    public Boolean getBooleanValue(Map<String, Object> object, String name);
    
    public Double getDoubleValue(Map<String, Object> object, String name);
    
    public String getStringValue(Map<String, Object> object, String name);
    
    public List<Long> getLongListValue(Map<String, Object> object, String name);
    
    public List<String> getStringListValue(Map<String, Object> object, String name);
    
    public Map<String, Object> getObjectValue(Map<String, Object> object, String name);
    
    public List<Map<String, Object>> getObjectListValue(Map<String, Object> object, String name);
    
    public String getRoute();

    public String getSessionId();
}
