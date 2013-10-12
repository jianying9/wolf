package com.wolf.framework.dao.parser;

import com.wolf.framework.dao.annotation.ColumnTypeEnum;

/**
 *
 * @author aladdin
 */
public interface RColumnHandler {

    public String getColumnName();
    
    public ColumnTypeEnum getColumnType();
    
    public String getDesc();
}
