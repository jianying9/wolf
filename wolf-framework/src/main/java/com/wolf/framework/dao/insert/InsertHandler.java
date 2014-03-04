package com.wolf.framework.dao.insert;

import com.wolf.framework.dao.Entity;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface InsertHandler<T extends Entity> {

    /**
     * 返回key值
     *
     * @param entityMap
     * @return
     */
    public String insert(Map<String, String> entityMap);
    
    public void batchInsert(List<Map<String, String>> entityMapList);
}
