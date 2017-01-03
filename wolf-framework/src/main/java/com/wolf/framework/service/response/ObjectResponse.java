package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface ObjectResponse<T extends Entity> extends BaseResponse {
    
    public void setDataMap(Map<String, Object> dataMap);
    
    public void setData(String name, Object value);
    
    public void setEntity(T t);
}
