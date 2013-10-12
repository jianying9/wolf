package com.wolf.framework.redis;

import com.wolf.framework.dao.condition.InquireRedisIndexContext;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface RedisHandler {
    
    public Map<String, String> inquireByKey(String keyValue);
    
    public List<Map<String, String>> inquireBykeys(List<String> keyValueList);
    
    public String insert(Map<String, String> entityMap);
    
    public void batchInsert(List<Map<String, String>> entityMapList);
    
    public String update(Map<String, String> entityMap);
    
    public void batchUpdate(List<Map<String, String>> entityMapList);
    
    public void delete(String keyValue);
    
    public void batchDelete(List<String> keyValueList);
    
    public List<String> inquireKeysByIndex(InquireRedisIndexContext inquireRedisIndexContext);
    
    public long countByIndex(String indexName, String indexValue);
}
