package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.DatabaseHandler;
import com.wolf.framework.dao.delete.DeleteHandler;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.update.UpdateHandler;
import java.util.List;
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
    
}
