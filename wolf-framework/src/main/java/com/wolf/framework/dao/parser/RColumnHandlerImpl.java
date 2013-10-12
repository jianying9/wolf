package com.wolf.framework.dao.parser;

import com.wolf.framework.dao.annotation.ColumnTypeEnum;

/**
 *
 * @author aladdin
 */
public class RColumnHandlerImpl implements RColumnHandler{
    
    private final String columnName;
    private final ColumnTypeEnum columnTypeEnum;
    private final String desc;

    public RColumnHandlerImpl(String columnName, ColumnTypeEnum columnTypeEnum, String desc) {
        this.columnName = columnName;
        this.columnTypeEnum = columnTypeEnum;
        this.desc = desc;
    }
    
    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public ColumnTypeEnum getColumnType() {
        return this.columnTypeEnum;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
