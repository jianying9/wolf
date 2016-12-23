package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface ListResponse<T extends Entity> extends BaseServiceResponse {
    
    public void setDataMapList(List<Map<String, String>> dataMapList);
    
    public void setEntityList(List<T> tList);
    
    public void setNextIndex(long nextIndex);
}
