package com.wolf.framework.dao;

import java.lang.reflect.Field;


/**
 *
 * @author aladdin
 */
public interface ColumnHandler {

    public String getColumnName();
    
    public String getDataMap();
    
    public Field getField();
    
    public ColumnType getColumnType();
    
    public ColumnDataType getColumnDataType();
    
    public String getDesc();
    
    public Object getDefaultValue();
}
