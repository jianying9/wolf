package com.wolf.framework.dao.reids;

import com.wolf.framework.dao.reids.annotation.ColumnType;

/**
 *
 * @author aladdin
 */
public class ColumnHandlerImpl implements ColumnHandler {

    private final String columnName;
    private final ColumnType columnTypeEnum;
    private final String desc;
    private final String defaultValue;

    public ColumnHandlerImpl(String columnName, ColumnType columnTypeEnum, String desc, String defaultValue) {
        this.columnName = columnName;
        this.columnTypeEnum = columnTypeEnum;
        this.desc = desc;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public ColumnType getColumnType() {
        return this.columnTypeEnum;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }
}
