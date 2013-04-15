package com.wolf.framework.derby;

import com.wolf.framework.dao.condition.Condition;
import com.wolf.framework.dao.condition.InquireContext;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface DerbyHandler {

    public Map<String, String> inquireByKey(String keyValue);

    public List<Map<String, String>> inquireBykeys(List<String> keyValueList);

    public List<String> inquireKeys(InquireContext inquireContext);

    public List<Map<String, String>> inquire(InquireContext inquireContext);

    public int count(List<Condition> conditionList);

    public void insert(Map<String, String> entityMap);
    
    public Map<String, String> insertAndInquire(Map<String, String> entityMap);
    
    public void batchInsert(List<Map<String, String>> entityMapList);
    
    public void delete(String keyValue);
    
    public void batchDelete(List<String> keyValueList);
    
    public void update(Map<String, String> entityMap);
    
    public void batchUpdate(List<Map<String, String>> entityMapList);
    
    public Map<String, String> updateAndInquire(Map<String, String> entityMap);
}
