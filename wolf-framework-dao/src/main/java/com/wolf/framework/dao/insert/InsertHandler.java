package com.wolf.framework.dao.insert;

import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface InsertHandler {

    /**
     * 返回key值
     *
     * @param entityMap
     * @return
     */
    public String insert(Map<String, String> entityMap);
    
    public void batchInsert(List<Map<String, String>> entityMapList);
}
