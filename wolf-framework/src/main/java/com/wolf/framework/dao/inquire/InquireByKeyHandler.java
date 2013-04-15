package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface InquireByKeyHandler<T extends Entity> {

    public T inquireByKey(String keyValue);
    
    public List<T> inquireByKeys(List<String> keyValues);
    
    public Map<String, String> inquireMapByKey(String keyValue);
    
    public List<Map<String, String>> inquireMapByKeys(List<String> keyValues);
}
