package com.wolf.framework.dao;

/**
 *
 * @author aladdin
 */
public class ColumnHandlerImpl implements ColumnHandler {

    private final String columnName;
    private final String dataMap;
    private final ColumnType columnType;
    private final String desc;
    private final String defaultValue;

    public ColumnHandlerImpl(String columnName, ColumnType columnType, String desc, String defaultValue) {
        this.columnName = columnName;
        this.dataMap = this.columnName;
        this.columnType = columnType;
        this.desc = desc;
        this.defaultValue = defaultValue;
    }

    public ColumnHandlerImpl(String columnName, String dataMap, ColumnType columnType, String desc, String defaultValue) {
        this.columnName = columnName;
        this.dataMap = dataMap;
        this.columnType = columnType;
        this.desc = desc;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public String getDataMap() {
        return this.dataMap;
    }

    @Override
    public ColumnType getColumnType() {
        return this.columnType;
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
