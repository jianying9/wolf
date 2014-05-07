package com.wolf.framework.redis;

import com.wolf.framework.dao.condition.InquirePageContext;
import com.wolf.framework.dao.condition.InquireIndexPageContext;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface RedisHandler {

    public String getTableIndexKey();

    public String getColumnIndexName(String columnName, String columnValue);

    public Map<String, String> inquireByKey(String keyValue);

    public List<Map<String, String>> inquireBykeys(List<String> keyValueList);

    public String insert(Map<String, String> entityMap);

    public void batchInsert(List<Map<String, String>> entityMapList);

    public String update(Map<String, String> entityMap);
    
    public void updateKeySorce(String keyValue, long sorce);

    public void batchUpdate(List<Map<String, String>> entityMapList);

    public void delete(String keyValue);

    public void batchDelete(List<String> keyValueList);

    public List<String> inquireKeys(InquirePageContext inquirePageContext);

    public List<String> inquireKeysDESC(InquirePageContext inquirePageContext);

    public long count();

    public List<String> inquireKeysByIndex(InquireIndexPageContext inquireRedisIndexContext);

    public List<String> inquireKeysByIndexDESC(InquireIndexPageContext inquireRedisIndexContext);

    public long countByIndex(String indexName, String indexValue);

    public long increase(String keyValue, String columnName, long value);
}
