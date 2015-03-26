package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.cassandra.annotation.ColumnType;


/**
 *
 * @author aladdin
 */
public interface ColumnHandler {

    public String getColumnName();
    
    public ColumnType getColumnType();
    
    public String getDesc();
    
    public String getDefaultValue();
}
