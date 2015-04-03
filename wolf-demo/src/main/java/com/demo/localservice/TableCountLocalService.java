package com.demo.localservice;

import com.wolf.framework.local.Local;

/**
 *
 * @author jianying9
 */
public interface TableCountLocalService extends Local{
    
    public boolean exist(String tableName);
    
    public long nextKey(String tableName, long num);
}
