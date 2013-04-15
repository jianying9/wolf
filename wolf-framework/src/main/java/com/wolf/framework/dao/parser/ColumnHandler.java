package com.wolf.framework.dao.parser;

import com.wolf.framework.dao.annotation.ColumnTypeEnum;
import com.wolf.framework.data.DataHandler;

/**
 *
 * @author aladdin
 */
public interface ColumnHandler {

    public String getColumnName();
    
    public ColumnTypeEnum getColumnType();
    
    public String getDesc();
    
    public DataHandler getDataHandler();
}
