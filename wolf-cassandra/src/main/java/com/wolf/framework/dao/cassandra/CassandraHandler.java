package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.DatabaseHandler;
import com.wolf.framework.dao.delete.DeleteHandler;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.update.UpdateHandler;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public interface CassandraHandler extends DatabaseHandler, UpdateHandler, DeleteHandler, InsertHandler {

    public void addSet(String keyValue, String columnName, String value);

    public void addSet(String keyValue, String columnName, Set<String> values);

    public void removeSet(String keyValue, String columnName, String value);

    public void removeSet(String keyValue, String columnName, Set<String> values);

    public void clearSet(String keyValue, String columnName);

    public Set<String> getSet(String keyValue, String columnName);

    public void addList(String keyValue, String columnName, String value);

    public void addList(String keyValue, String columnName, List<String> values);

    public void addFirstList(String keyValue, String columnName, String value);

    public void addFirstList(String keyValue, String columnName, List<String> values);

    public void removeList(String keyValue, String columnName, String value);

    public void removeList(String keyValue, String columnName, List<String> values);

    public void clearList(String keyValue, String columnName);

    public List<String> getList(String keyValue, String columnName);

    public void addMap(String keyValue, String columnName, String mapKeyValue, String mapValue);

    public void addMap(String keyValue, String columnName, Map<String, String> values);

    public void removeMap(String keyValue, String columnName, String mapKeyValue);

    public void removeMap(String keyValue, String columnName, Set<String> mapKeyValues);

    public void clearMap(String keyValue, String columnName);

    public Map<String, String> getMap(String keyValue, String columnName);
    
    public long increase(String keyValue, String columnName, long value);
}
