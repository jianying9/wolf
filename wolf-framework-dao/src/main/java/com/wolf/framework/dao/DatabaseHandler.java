package com.wolf.framework.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface DatabaseHandler {
    
    /**
     * 判断某个主键值是否存在
     * @param keyValue
     * @return 
     */
    public boolean exist(String keyValue);
    
    /**
     * 根据主键查询一行记录
     * @param keyValue
     * @return 
     */
    public Map<String, String> inquireByKey(String keyValue);
    
    /**
     * 根据主键集合查询多行记录
     * @param keyValueList
     * @return 
     */
    public List<Map<String, String>> inquireBykeys(List<String> keyValueList);
    
    /**
     * 插入一行记录
     * @param entityMap
     * @return 
     */
    public String insert(Map<String, String> entityMap);
    
    /**
     * 更新一行记录
     * @param entityMap
     * @return 
     */
    public String update(Map<String, String> entityMap);
    
    /**
     * 删除一行记录
     * @param keyValue 
     */
    public void delete(String keyValue);
    
    /**
     * 查询总记录数
     * @return 
     */
    public long count();
}
