package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface ListServiceResponse<T extends Entity> {
    
    public String getState();
    
    public void setState(String state);
    
    public void setDataMapList(List<Map<String, String>> dataMapList);
    
    public void setEntityList(List<T> tList);
    
    public void success();
    
    public void failure();
    
    public void setNextIndex(String nextIndex);
    
    public void setNextSize(int nextSize);
}
