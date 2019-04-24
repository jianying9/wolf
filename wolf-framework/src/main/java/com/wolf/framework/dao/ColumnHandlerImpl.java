package com.wolf.framework.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;

/**
 *
 * @author aladdin
 */
public class ColumnHandlerImpl implements ColumnHandler {

    private final String columnName;
    private final String dataMap;
    private final ColumnType columnType;
    protected final ColumnDataType columnDataType;
    private ColumnDataType firstParameterDataType = null;
    private ColumnDataType secondParameterDataType = null;
    protected final Field field;
    private final String desc;
    private final Object defaultValue;

    public ColumnHandlerImpl(String columnName, String dataMap, Field field, ColumnType columnType, String desc, String defaultValue) {
        this.columnName = columnName;
        this.dataMap = dataMap;
        this.columnType = columnType;
        this.field = field;
        String type = this.field.getType().getName();
        this.columnDataType = FieldUtils.getColumnDataType(type);
        switch (this.columnDataType) {
            case LONG:
                if (defaultValue.isEmpty()) {
                    this.defaultValue = 0l;
                } else {
                    this.defaultValue = Long.parseLong(defaultValue);
                }
                break;
            case INT:
                if (defaultValue.isEmpty()) {
                    this.defaultValue = 0;
                } else {
                    this.defaultValue = Integer.parseInt(defaultValue);
                }
                break;
            case BOOLEAN:
                if (defaultValue.isEmpty()) {
                    this.defaultValue = false;
                } else {
                    this.defaultValue = Boolean.parseBoolean(defaultValue);
                }
                break;
            case DOUBLE:
                if (defaultValue.isEmpty()) {
                    this.defaultValue = 0.0;
                } else {
                    this.defaultValue = Double.parseDouble(defaultValue);
                }
                break;
            case STRING:
                this.defaultValue = defaultValue;
                break;
            case LIST:
                this.defaultValue = Collections.EMPTY_LIST;
                ParameterizedType listGenericType = (ParameterizedType) this.field.getGenericType();
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                this.firstParameterDataType = FieldUtils.getColumnDataType(listActualTypeArguments[0].getTypeName());
                break;
            case SET:
                this.defaultValue = Collections.EMPTY_SET;
                ParameterizedType setGenericType = (ParameterizedType) this.field.getGenericType();
                Type[] setActualTypeArguments = setGenericType.getActualTypeArguments();
                this.firstParameterDataType = FieldUtils.getColumnDataType(setActualTypeArguments[0].getTypeName());
                break;
            case MAP:
                this.defaultValue = Collections.EMPTY_MAP;
                ParameterizedType mapGenericType = (ParameterizedType) this.field.getGenericType();
                Type[] mapActualTypeArguments = mapGenericType.getActualTypeArguments();
                this.firstParameterDataType = FieldUtils.getColumnDataType(mapActualTypeArguments[0].getTypeName());
                this.secondParameterDataType = FieldUtils.getColumnDataType(mapActualTypeArguments[1].getTypeName());
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
    public Object getFieldValue(Object object) {
        Object result = null;
        try {
            this.field.setAccessible(true);
            result = field.get(object);
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

    @Override
    public ColumnDataType getFirstParameterDataType() {
        return firstParameterDataType;
    }

    @Override
    public ColumnDataType getSecondParameterDataType() {
        return secondParameterDataType;
    }

}
