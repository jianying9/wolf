package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface ServiceResponse<T extends Entity> extends BaseServiceResponse{
    
    public void setDataMap(Map<String, String> dataMap);
    
    public void setEntity(T t);
}
