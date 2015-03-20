package com.wolf.framework.dao.parser;

import com.wolf.framework.dao.annotation.ColumnType;

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
