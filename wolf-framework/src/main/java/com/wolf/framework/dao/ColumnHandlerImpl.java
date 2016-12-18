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
                if (defaultValue.isEmpty()) {
                    this.defaultValue = 0;
                } else {
                    this.defaultValue = Long.parseLong(defaultValue);
                }
                break;
            case "int":
            case "java.lang.Integer":
                this.columnDataType = ColumnDataType.INT;
                if (defaultValue.isEmpty()) {
                    this.defaultValue = 0;
                } else {
                    this.defaultValue = Integer.parseInt(defaultValue);
                }
                break;
            case "boolean":
            case "java.lang.Boolean":
                this.columnDataType = ColumnDataType.BOOLEAN;
                if (defaultValue.isEmpty()) {
                    this.defaultValue = false;
                } else {
                    this.defaultValue = Boolean.parseBoolean(defaultValue);
                }
                break;
            case "double":
            case "java.lang.Double":
                this.columnDataType = ColumnDataType.DOUBLE;
                if (defaultValue.isEmpty()) {
                    this.defaultValue = 0;
                } else {
                    this.defaultValue = Double.parseDouble(defaultValue);
                }
                break;
            case "java.lang.String":
                this.columnDataType = ColumnDataType.STRING;
                this.defaultValue = defaultValue;
                break;
            default:
                throw new RuntimeException("Entity not support this type:" + type);
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

    @Override
    public String getFieldStringValue(Object object) {
        String result = "";
        try {
            this.field.setAccessible(true);
            switch (this.columnDataType) {
                case LONG:
                    result = Long.toString(this.field.getLong(object));
                    break;
                case INT:
                    result = Integer.toString(this.field.getInt(object));
                    break;
                case DOUBLE:
                    result = Double.toString(this.field.getDouble(object));
                    break;
                case STRING:
                    result = (String) this.field.get(object);
                    break;
                case BOOLEAN:
                    result = Boolean.toString(this.field.getBoolean(object));
                    break;
            }
            this.field.setAccessible(false);
            return result;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
        }
        return result;
    }

    @Override
    public void setFieldValue(Object object, Object value) {
        try {
            this.field.setAccessible(true);
            this.field.set(object, value);
            this.field.setAccessible(false);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
        }
    }

}
