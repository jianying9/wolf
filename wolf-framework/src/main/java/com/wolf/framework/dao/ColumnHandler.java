package com.wolf.framework.dao;


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
