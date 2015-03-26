package com.wolf.framework.dao.reids;

import com.wolf.framework.dao.reids.annotation.ColumnType;

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
