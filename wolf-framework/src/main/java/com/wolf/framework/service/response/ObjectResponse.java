package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface ObjectResponse<T extends Entity> extends BaseServiceResponse {
    
    public void setDataMap(Map<String, String> dataMap);
    
    public void setData(String name, String value);
    
    public void setEntity(T t);
}
