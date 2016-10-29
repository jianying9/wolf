package com.wolf.framework.dao;

import java.lang.reflect.Field;

/**
 *
 * @author aladdin
 */
public class ColumnHandlerImpl implements ColumnHandler {

    private final String columnName;
    private final String dataMap;
    private final ColumnType columnType;
    private final ColumnDataType columnDataType;
    private final Field field;
    private final String desc;
    private final Object defaultValue;

    public ColumnHandlerImpl(String columnName, String dataMap, Field field, ColumnType columnType, String desc, String defaultValue) {
        this.columnName = columnName;
        this.dataMap = dataMap;
        this.columnType = columnType;
        this.field = field;
        String type = this.field.getType().getName();
        switch (type) {
            case "long":
            case "java.lang.Long":
                this.columnDataType = ColumnDataType.LONG;
                if(defaultValue.isEmpty()) {
                    this.defaultValue = 0;
                } else {
                    this.defaultValue = Long.parseLong(defaultValue);
                }   break;
            case "int":
            case "java.lang.Integer":
                this.columnDataType = ColumnDataType.INT;
                if(defaultValue.isEmpty()) {
                    this.defaultValue = 0;
                } else {
                    this.defaultValue = Integer.parseInt(defaultValue);
                }   break;
            case "boolean":
            case "java.lang.Boolean":
                this.columnDataType = ColumnDataType.BOOLEAN;
                if(defaultValue.isEmpty()) {
                    this.defaultValue = false;
                } else {
                    this.defaultValue = Boolean.parseBoolean(defaultValue);
                }   break;
            case "double":
            case "java.lang.Double":
                this.columnDataType = ColumnDataType.DOUBLE;
                if(defaultValue.isEmpty()) {
                    this.defaultValue = 0;
                } else {
                    this.defaultValue = Double.parseDouble(defaultValue);
                }   break;
            default:
                this.columnDataType = ColumnDataType.STRING;
                this.defaultValue = defaultValue;
                break;
        }
        this.desc = desc;
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
    public Field getField() {
        return this.field;
    }

    @Override
    public ColumnType getColumnType() {
        return this.columnType;
    }
    
    @Override
    public ColumnDataType getColumnDataType() {
        return this.columnDataType;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
}
